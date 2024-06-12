package ru.practicum.ewm.service.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.service.category.repository.CategoryRepository;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventRepositoryImpl eventRepositoryImpl;
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private StatisticClient statClient;

    public Event getEventById(long eventId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            throw new EventNotFoundException("Событие " + eventId + " не найдено");
        } else {
            return optionalEvent.get();
        }
    }

    /**
     * Получение подробной информации об опубликованном событии по его идентификатору
     *
     * @param eventId id события
     * @return полную информацию о событии
     */
    public Event getEventByIdPublic(long eventId, String requestUri, String remoteIp) {
        statClient = new StatisticClient(requestUri);
        Event event = getEventById(eventId);
        //событие должно быть опубликовано
        if (event.getState() != EventState.PUBLISHED) {
            throw new EventNotFoundException("Событие " + eventId + " не найдено");
        }

        //информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
        RecordStatisticDto record = new RecordStatisticDto();
        record.setIp(remoteIp);
        record.setTimestamp(LocalDateTime.now().toString());
        statClient.sendHit(record);
        return event;
    }

    public int getEventsCountByCategoryId(long categoryId) {
        return eventRepository.getEventsCountByCategoryId(categoryId);
    }

    public List<Event> getAllEventsAdmin(List<Integer> usersIdList,
                                         List<EventState> states,
                                         List<Integer> categoriesIdList,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         int from,
                                         int size) {
        return eventRepositoryImpl.findAllEventsByFilterAdmin(usersIdList, states, categoriesIdList, rangeStart, rangeEnd, from, size);
    }

    /**
     * Получение событий с возможностью фильтрации, публичный эндпоинт
     *
     * @param text             текст для поиска в содержимом аннотации и подробном описании события
     * @param categoriesIdList список идентификаторов категорий в которых будет вестись поиск
     * @param paid             поиск только платных/бесплатных событий
     * @param rangeStart       дата и время не раньше которых должно произойти событие
     * @param rangeEnd         дата и время не позже которых должно произойти событие
     * @param onlyAvailable    только события у которых не исчерпан лимит запросов на участие
     * @param sort             Вариант сортировки: по дате события или по количеству просмотров, аvailable values : EVENT_DATE, VIEWS
     * @param from             количество событий, которые нужно пропустить для формирования текущего набора
     * @param size             количество событий в наборе
     * @return список событий Event
     */
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
        //информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
        statClient.sendHit(requestUri, remoteIp, LocalDateTime.now());

        return eventRepositoryImpl.findAllEventsByFilterPublic(text, categoriesIdList, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    public Event updateEventByAdmin(UpdateEventAdminRequest updateRequest, long eventId) {
        Event storageEvent = getEventById(eventId);

        //событие можно отклонить, только если оно еще не опубликовано (Ожидается код ошибки 409)
        if (updateRequest.getStateAction() != null && "REJECT_EVENT".equals(updateRequest.getStateAction())) {
            if (storageEvent.getState() != EventState.PENDING) {
                throw new EventUpdateException("Cannot cancel the event because it's not in the right state: " + storageEvent.getState().toString());
            } else {
                storageEvent.setState(EventState.CANCELED);
            }
        }

        //событие можно публиковать, только если оно в состоянии ожидания публикации (Ожидается код ошибки 409)
        if (updateRequest.getStateAction() != null && "PUBLISH_EVENT".equals(updateRequest.getStateAction())) {
            if (storageEvent.getState() != EventState.PENDING) {
                throw new EventUpdateException("Cannot publish the event because it's not in the right state: " + storageEvent.getState().toString());
            } else {
                storageEvent.setState(EventState.PUBLISHED);
                storageEvent.setPublishedOn(LocalDateTime.now());
            }
        }

        //дата начала изменяемого события должна быть не ранее чем за час от даты публикации. (Ожидается код ошибки 409)
        if (updateRequest.getEventDate() != null) {
            LocalDateTime newEventDate = LocalDateTime.parse(updateRequest.getEventDate(), EwmConstants.DATE_TIME_FORMATTER);
            if (newEventDate.isBefore(LocalDateTime.now().plusHours(1))) {
                throw new EventUpdateException("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
            } else {
                storageEvent.setEventDate(newEventDate);
            }
        }

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

        return eventRepository.save(storageEvent);
    }

    public List<Event> getUserEvents(long userId, int from, int size) {
        Pageable page = PaginationCalculator.getPage(from, size);
        return eventRepository.findByInitiator_idOrderById(userId, page);
    }

    public Event createEvent(NewEventDto newEventDto, long userId) {
        //дата начала изменяемого события должна быть не ранее чем за два часа от даты публикации. (Ожидается код ошибки 409)
        if (LocalDateTime.parse(newEventDto.getEventDate(), EwmConstants.DATE_TIME_FORMATTER).isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EventUpdateException("Дата начала изменяемого события должна быть не ранее чем за два часа от даты публикации");
        }

        Event event = Event.builder()
                .category(categoryRepository.getReferenceById(newEventDto.getCategory()))
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .eventDate(LocalDateTime.parse(newEventDto.getEventDate(), EwmConstants.DATE_TIME_FORMATTER))
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

        return eventRepository.save(event);
    }

    public Event getEventByIdAndInitiatorId(long eventId, long initiatorId) {
        Optional<Event> optionalEvent = eventRepository.findByIdAndInitiator_id(eventId, initiatorId);
        if (optionalEvent.isEmpty()) {
            throw new EventNotFoundException("Событие " + eventId + " не найдено или недоступно");
        } else {
            return optionalEvent.get();
        }
    }

    public Event updateEventByInitiator(UpdateEventUserRequest eventRequest, long eventId, long initiatorId) {
        Event storageEvent = getEventByIdAndInitiatorId(eventId, initiatorId);

        //изменить можно только отмененные события или события в состоянии ожидания модерации (Ожидается код ошибки 409)
        if (storageEvent.getState() == EventState.PUBLISHED) {
            throw new EventUpdateException("Изменить можно только отмененные события или события в состоянии ожидания модерации");
        }

        //поиск и проверка изменяемых полей
        if (eventRequest.getAnnotation() != null) {
            storageEvent.setAnnotation(eventRequest.getAnnotation());
        }

        if (eventRequest.getCategory() != null) {
            storageEvent.setCategory(categoryRepository.getReferenceById(eventRequest.getCategory()));
        }

        if (eventRequest.getDescription() != null) {
            storageEvent.setDescription(eventRequest.getDescription());
        }

        if (eventRequest.getEventDate() != null) {
            LocalDateTime eventRequestDate = LocalDateTime.parse(eventRequest.getEventDate(), EwmConstants.DATE_TIME_FORMATTER);

            //дата начала изменяемого события должна быть не ранее чем за два часа от даты публикации. (Ожидается код ошибки 409)
            if (eventRequestDate.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new EventUpdateException("Дата начала изменяемого события должна быть не ранее чем за два часа от даты публикации");
            }

            storageEvent.setEventDate(eventRequestDate);
        }

        if (eventRequest.getLocation() != null) {
            storageEvent.setLocation(eventRequest.getLocation());
        }

        if (eventRequest.getPaid() != null) {
            storageEvent.setPaid(eventRequest.getPaid());
        }

        if (eventRequest.getParticipantLimit() != null) {
            storageEvent.setParticipantLimit(eventRequest.getParticipantLimit());
        }

        if (eventRequest.getRequestModeration() != null) {
            storageEvent.setRequestModeration(eventRequest.getRequestModeration());
        }

        if (eventRequest.getTitle() != null) {
            storageEvent.setTitle(eventRequest.getTitle());
        }

        if (eventRequest.getStateAction() != null) {
            switch (eventRequest.getStateAction()) {
                case "SEND_TO_REVIEW":
                    storageEvent.setState(EventState.PENDING);
                    break;
                case "CANCEL_REVIEW":
                    storageEvent.setState(EventState.CANCELED);
                    break;
            }
        }

        return eventRepository.save(storageEvent);
    }
}