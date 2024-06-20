package ru.practicum.ewm.service.request.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.event.model.EventState;
import ru.practicum.ewm.service.event.repository.EventRepository;
import ru.practicum.ewm.service.event.service.EventService;
import ru.practicum.ewm.service.exceptions.*;
import ru.practicum.ewm.service.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.service.request.repository.ParticipationRepository;
import ru.practicum.ewm.service.request.model.ParticipationDtoMapper;
import ru.practicum.ewm.service.request.model.Participation;
import ru.practicum.ewm.service.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.service.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.service.request.model.RequestStatus;
import ru.practicum.ewm.service.user.model.User;
import ru.practicum.ewm.service.user.service.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.service.event.service.EventService.localDateTimeToStringConverter;
import static ru.practicum.ewm.service.event.service.EventService.stringToLocalDateTimeConverter;

@Service
@RequiredArgsConstructor
public class ParticipationService {
    private final ParticipationRepository participationRepository;
    private final EventRepository eventRepository;
    private final EventService eventService;
    private final UserService userService;
    private final ModelMapper mapper = new ModelMapper();

    public static final Converter<User, Long> userLongConverter = new AbstractConverter<>() {
        @Override
        protected Long convert(User source) {
            return source.getId();
        }
    };

    public static final Converter<Event, Long> eventLongConverter = new AbstractConverter<>() {
        @Override
        protected Long convert(Event source) {
            return source.getId();
        }
    };

    public static PropertyMap<Participation, ParticipationRequestDto> propertyMap = new PropertyMap<>() {
        @Override
        protected void configure() {
            using(userLongConverter).map(source.getUser(), destination.getRequester());
        }
    };


    public Participation getRequestById(long requestId) {
        return participationRepository.findById(requestId)
                .orElseThrow(() -> new ParticipationRequestNotFoundException("Запрос " + requestId + " не найден"));
    }

    public List<ParticipationRequestDto> getRequestsByUserId(long userId) {
        userService.getUserById(userId);
        return participationRepository.findByUser_IdOrderById(userId)
                .stream()
                .map(participation -> mapper.map(participation, ParticipationRequestDto.class))
                .collect(Collectors.toList());
    }

    public List<ParticipationRequestDto> getRequestsByEventIdAndInitiatorId(long eventId, long initiatorId) {
        eventService.getEventByIdAndInitiatorId(eventId, initiatorId);
        return participationRepository.findByEvent_IdOrderById(eventId)
                .stream()
                .map(participation -> mapper.map(participation, ParticipationRequestDto.class))
                .collect(Collectors.toList());
    }

    private Participation getRequestByEventIdAndRequesterId(long eventId, long requesterId) {
        eventService.getEventById(eventId);
        return participationRepository.findByEvent_IdAndUser_Id(eventId, requesterId)
                .orElse(null);
    }

    public EventRequestStatusUpdateResult updateRequestsStatus(EventRequestStatusUpdateRequest updateRequest, long eventId, long initiatorId) {
        EventFullDto eventFullDto = eventService.getEventByIdAndInitiatorId(eventId, initiatorId);
        mapper.addConverter(localDateTimeToStringConverter);
        mapper.addConverter(stringToLocalDateTimeConverter);
        Event event = mapper.map(eventFullDto, Event.class);
        String newStatus = updateRequest.getStatus();
        int participantLimit = event.getParticipantLimit();
        int confirmedRequests = event.getConfirmedRequests();

        if (participantLimit > 0 && event.getRequestModeration()) {
            if ("CONFIRMED".equals(newStatus)) {
                if (confirmedRequests >= participantLimit) {
                    throw new ParticipationRequestLimitException("Достигнут лимит по заявкам на событие " + eventId);
                }
            }

            for (Long requestId : updateRequest.getRequestIds()) {
                Participation storageRequest = getRequestById(requestId);

                if (RequestStatus.PENDING.toString().equals(storageRequest.getStatus())) {
                    if ("CONFIRMED".equals(newStatus)) {
                        if (confirmedRequests++ < participantLimit) {
                            storageRequest.setStatus(newStatus);
                            participationRepository.save(storageRequest);

                            if (confirmedRequests == participantLimit) {
                                rejectAllPendingRequests(eventId);
                                break;
                            }
                        }
                    } else {
                        storageRequest.setStatus(newStatus);
                        participationRepository.save(storageRequest);
                    }
                } else {
                    throw new ParticipationRequestInvalidStateException("Неверное состояние заявки " + requestId + " перед модерацией");
                }
            }
        }

        EventRequestStatusUpdateResult updateResult = getEventRequestStatusUpdateResult(eventId);
        event.setConfirmedRequests(updateResult.getConfirmedRequests().size());
        eventRepository.save(event);

        return updateResult;
    }

    private void rejectAllPendingRequests(long eventId) {
        participationRepository.rejectAllPendingRequests(eventId);
    }

    private EventRequestStatusUpdateResult getEventRequestStatusUpdateResult(long eventId) {
        List<Participation> confirmed = participationRepository.findByEvent_IdAndStatusOrderById(eventId, RequestStatus.CONFIRMED.toString());
        List<Participation> rejected = participationRepository.findByEvent_IdAndStatusOrderById(eventId, RequestStatus.REJECTED.toString());

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(ParticipationDtoMapper.toParticipationRequestDtoList(confirmed))
                .rejectedRequests(ParticipationDtoMapper.toParticipationRequestDtoList(rejected))
                .build();
    }

    public ParticipationRequestDto createParticipationRequest(long eventId, long requesterId) {
        Event event = eventService.getEventById(eventId);
        User requester = userService.getUserById(requesterId);

        if (event.getInitiator().getId() == requesterId) {
            throw new ParticipationRequestInitiatorException("Инициатор события " + eventId + " не может добавить запрос на участие в своём событии");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ParticipationRequestEventNotPublishedException("Событие " + eventId + " не опубликовано");
        }

        Participation oldRequest = getRequestByEventIdAndRequesterId(eventId, requesterId);
        if (oldRequest != null) {
            throw new ParticipationRequestDuplicationException("Нельзя добавить повторный запрос в событие " + eventId);
        }

        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ParticipationRequestLimitReachedException("Достигнут лимит запросов на участие в событии " + eventId);
        }

        String newStatus;
        if (event.getParticipantLimit() > 0 && event.getRequestModeration()) {
            newStatus = RequestStatus.PENDING.toString();
        } else {
            newStatus = RequestStatus.CONFIRMED.toString();
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }

        Participation request = Participation.builder()
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .event(event)
                .user(requester)
                .status(newStatus)
                .build();
        mapper.addConverter(eventLongConverter);
        mapper.addConverter(userLongConverter);
        mapper.addMappings(propertyMap);
        return mapper.map(participationRepository.save(request), ParticipationRequestDto.class);
    }

    public ParticipationRequestDto cancelParticipationRequest(long requestId, long userId) {
        Participation request = getRequestById(requestId);
        if (request.getUser().getId() != userId) {
            throw new ParticipationRequestNotFoundException("Запрос " + requestId + " не найден");
        }

        String oldStatus = request.getStatus();
        request.setStatus(RequestStatus.CANCELED.toString());
        Participation storageRequest = participationRepository.save(request);

        if (RequestStatus.CONFIRMED.toString().equals(oldStatus)) {
            Event event = storageRequest.getEvent();
            if (event.getRequestModeration() && event.getParticipantLimit() > 0) {
                event.setConfirmedRequests(event.getConfirmedRequests() - 1);
            }
            eventRepository.save(event);
        }
        return mapper.map(storageRequest, ParticipationRequestDto.class);
    }
}