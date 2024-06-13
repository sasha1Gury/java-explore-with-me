package ru.practicum.ewm.service.event.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.service.category.repository.CategoryRepository;
import ru.practicum.ewm.service.event.dto.*;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.event.model.EventState;
import ru.practicum.ewm.service.event.repository.EventRepository;
import ru.practicum.ewm.service.event.repository.EventRepositoryImpl;
import ru.practicum.ewm.service.exceptions.EventNotFoundException;
import ru.practicum.ewm.service.exceptions.EventUpdateException;
import ru.practicum.ewm.service.user.service.UserService;
import ru.practicum.stat.client.StatisticClient;
import ru.practicum.stat.common.dto.RecordStatisticDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.service.error_handler.ErrorHandler.DATE_TIME_FORMATTER;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventRepositoryImpl eventRepositoryImpl;
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final ModelMapper mapper = new ModelMapper();

    public Event getEventById(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Событие " + eventId + " не найдено"));
    }

    public EventFullDto getEventByIdPublic(long eventId, String requestUri, String remoteIp) {
        Event event = getEventById(eventId);
        if (event.getState() != EventState.PUBLISHED) {
            throw new EventNotFoundException("Событие " + eventId + " не найдено");
        }

        recordStatistics(requestUri, remoteIp);
        return mapper.map(event, EventFullDto.class);
    }

    public int getEventsCountByCategoryId(long categoryId) {
        return eventRepository.getEventsCountByCategoryId(categoryId);
    }

    public List<EventFullDto> getAllEventsAdmin(List<Integer> usersIdList,
                                                List<EventState> states,
                                                List<Integer> categoriesIdList,
                                                LocalDateTime rangeStart,
                                                LocalDateTime rangeEnd,
                                                int from,
                                                int size) {
        return eventRepositoryImpl.findAllEventsByFilterAdmin(usersIdList, states, categoriesIdList, rangeStart, rangeEnd, from, size)
                .stream()
                .map(event -> mapper.map(event, EventFullDto.class))
                .collect(Collectors.toList());
    }

    public List<Event> getAllEventsPublic(String text,
                                          List<Integer> categoriesIdList,
                                          Boolean paid,
                                          LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd,
                                          Boolean onlyAvailable,
                                          String sort,
                                          int from,
                                          int size,
                                          String requestUri,
                                          String remoteIp) {
        recordStatistics(requestUri, remoteIp);
        return eventRepositoryImpl.findAllEventsByFilterPublic(text, categoriesIdList, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    public EventFullDto updateEventByAdmin(UpdateEventRequest updateRequest, long eventId) {
        Event storageEvent = getEventById(eventId);

        handleStateAction(updateRequest.getStateAction(), storageEvent);

        if (updateRequest.getEventDate() != null) {
            LocalDateTime newEventDate = LocalDateTime.parse(updateRequest.getEventDate(), DATE_TIME_FORMATTER);
            validateEventDate(newEventDate);
            storageEvent.setEventDate(newEventDate);
        }

        updateEventFields(updateRequest, storageEvent);

        return mapper.map(eventRepository.save(storageEvent), EventFullDto.class);
    }

    public List<EventShortDto> getUserEvents(long userId, int from, int size) {
        Pageable page = PageRequest.of(from / size, size);
        return eventRepository.findByInitiator_idOrderById(userId, page)
                .stream()
                .map(event -> mapper.map(event, EventShortDto.class))
                .collect(Collectors.toList());
    }

    public EventFullDto createEvent(NewEventDto newEventDto, long userId) {
        LocalDateTime eventDate = LocalDateTime.parse(newEventDto.getEventDate(), DATE_TIME_FORMATTER);
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EventUpdateException("Дата начала изменяемого события должна быть не ранее чем за два часа от даты публикации");
        }

        Event event = Event.builder()
                .category(categoryRepository.getReferenceById(newEventDto.getCategory()))
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .eventDate(eventDate)
                .location(newEventDto.getLocation())
                .paid(newEventDto.isPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.isRequestModeration())
                .title(newEventDto.getTitle())
                .createdOn(LocalDateTime.now())
                .publishedOn(LocalDateTime.now())
                .initiator(userService.getUserById(userId))
                .state(EventState.PENDING)
                .build();

        return mapper.map(eventRepository.save(event), EventFullDto.class);
    }

    public EventFullDto getEventByIdAndInitiatorId(long eventId, long initiatorId) {
        Event event = eventRepository.findByIdAndInitiator_id(eventId, initiatorId)
                .orElseThrow(() -> new EventNotFoundException("Событие " + eventId + " не найдено или недоступно"));

        return mapper.map(event, EventFullDto.class);
    }

    public EventFullDto updateEventByInitiator(UpdateEventRequest eventRequest, long eventId, long initiatorId) {
        EventFullDto eventFullDto = getEventByIdAndInitiatorId(eventId, initiatorId);
        Event storageEvent = mapper.map(eventFullDto, Event.class);

        if (storageEvent.getState() == EventState.PUBLISHED) {
            throw new EventUpdateException("Изменить можно только отмененные события или события в состоянии ожидания модерации");
        }

        if (eventRequest.getEventDate() != null) {
            LocalDateTime eventRequestDate = LocalDateTime.parse(eventRequest.getEventDate(), DATE_TIME_FORMATTER);
            if (eventRequestDate.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new EventUpdateException("Дата начала изменяемого события должна быть не ранее чем за два часа от даты публикации");
            }
            storageEvent.setEventDate(eventRequestDate);
        }

        updateEventFields(eventRequest, storageEvent);

        if (eventRequest.getStateAction() != null) {
            handleStateAction(eventRequest.getStateAction(), storageEvent);
        }

        return mapper.map(eventRepository.save(storageEvent), EventFullDto.class);
    }

    private void handleStateAction(String stateAction, Event storageEvent) {
        if (stateAction == null) {
            return;
        }

        switch (stateAction) {
            case "REJECT_EVENT":
                validateEventState(storageEvent, "Cannot cancel the event because it's not in the right state: ");
                storageEvent.setState(EventState.CANCELED);
                break;
            case "PUBLISH_EVENT":
                validateEventState(storageEvent, "Cannot publish the event because it's not in the right state: ");
                storageEvent.setState(EventState.PUBLISHED);
                storageEvent.setPublishedOn(LocalDateTime.now());
                break;
            case "SEND_TO_REVIEW":
                storageEvent.setState(EventState.PENDING);
                break;
            case "CANCEL_REVIEW":
                storageEvent.setState(EventState.CANCELED);
                break;
            default:
                throw new IllegalArgumentException("Unknown state action: " + stateAction);
        }
    }

    private void validateEventState(Event storageEvent, String errorMessage) {
        if (storageEvent.getState() != EventState.PENDING) {
            throw new EventUpdateException(errorMessage + storageEvent.getState().toString());
        }
    }

    private void validateEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new EventUpdateException("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
        }
    }

    private void updateEventFields(UpdateEventRequest updateRequest, Event storageEvent) {
        if (updateRequest.getAnnotation() != null) {
            storageEvent.setAnnotation(updateRequest.getAnnotation());
        }

        if (updateRequest.getCategory() != null) {
            storageEvent.setCategory(categoryRepository.getReferenceById(updateRequest.getCategory()));
        }

        if (updateRequest.getDescription() != null) {
            storageEvent.setDescription(updateRequest.getDescription());
        }

        if (updateRequest.getLocation() != null) {
            storageEvent.setLocation(updateRequest.getLocation());
        }

        if (updateRequest.getPaid() != null) {
            storageEvent.setPaid(updateRequest.getPaid());
        }

        if (updateRequest.getParticipantLimit() != null) {
            storageEvent.setParticipantLimit(updateRequest.getParticipantLimit());
        }

        if (updateRequest.getRequestModeration() != null) {
            storageEvent.setRequestModeration(updateRequest.getRequestModeration());
        }

        if (updateRequest.getTitle() != null) {
            storageEvent.setTitle(updateRequest.getTitle());
        }
    }

    private void recordStatistics(String requestUri, String remoteIp) {
        StatisticClient statClient = new StatisticClient(requestUri);
        RecordStatisticDto stat = RecordStatisticDto.builder()
                .app("ewm-main-service")
                .uri(requestUri)
                .ip(remoteIp)
                .timestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                .build();

        statClient.sendHit(stat);
    }
}