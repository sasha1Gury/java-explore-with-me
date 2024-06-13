package ru.practicum.ewm.service.event.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.event.model.EventState;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventRepositoryImpl {
    private final EntityManager entityManager;

    public List<Event> findAllEventsByFilterAdmin(List<Integer> usersIdList,
                                                  List<EventState> states,
                                                  List<Integer> categoriesIdList,
                                                  LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd,
                                                  int from,
                                                  int size) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> cbQuery = cb.createQuery(Event.class);
        Root<Event> root = cbQuery.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();
        if (usersIdList != null && !usersIdList.isEmpty()) {
            predicates.add(root.get("initiator").get("id").in(usersIdList));
        }

        if (states != null && !states.isEmpty()) {
            predicates.add(root.get("state").in(states));
        }

        if (categoriesIdList != null && !categoriesIdList.isEmpty()) {
            predicates.add(root.get("category").get("id").in(categoriesIdList));
        }

        if (rangeStart != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }

        if (rangeEnd != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }

        cbQuery.select(root).where(predicates.toArray(new Predicate[0])).orderBy(cb.asc(root.get("id")));

        return entityManager.createQuery(cbQuery).setFirstResult(from).setMaxResults(size).getResultList();
    }

    public List<Event> findAllEventsByFilterPublic(String text,
                                                   List<Integer> categoriesIdList,
                                                   Boolean paid,
                                                   LocalDateTime rangeStart,
                                                   LocalDateTime rangeEnd,
                                                   Boolean onlyAvailable,
                                                   String sort,
                                                   int from,
                                                   int size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> cbQuery = cb.createQuery(Event.class);
        Root<Event> root = cbQuery.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();

        //это публичный эндпоинт, соответственно в выдаче должны быть только опубликованные события
        predicates.add(cb.equal(root.get("state").as(String.class), EventState.PUBLISHED.name()));

        //текстовый поиск (по аннотации и подробному описанию) должен быть без учета регистра букв
        if (text != null && !text.isEmpty()) {
            predicates.add(
                    cb.or(
                            cb.like(cb.upper(root.get("annotation")), "%" + text.toUpperCase() + "%"),
                            cb.like(cb.upper(root.get("description")), "%" + text.toUpperCase() + "%")
                    ));
        }

        if (categoriesIdList != null && !categoriesIdList.isEmpty()) {
            predicates.add(root.get("category").get("id").in(categoriesIdList));
        }

        if (paid != null) {
            predicates.add(cb.equal(root.get("paid"), paid));
        }

        if (rangeStart != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }

        if (rangeEnd != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }

        //если в запросе не указан диапазон дат [rangeStart-rangeEnd], то нужно выгружать события, которые произойдут позже текущей даты и времени
        if (rangeStart == null && rangeEnd == null) {
            predicates.add(cb.greaterThan(root.get("eventDate"), LocalDateTime.now()));
        }

        if (onlyAvailable != null && onlyAvailable) {
            predicates.add(
                    cb.or(
                            cb.equal(root.get("participantLimit"), 0),
                            cb.lessThan(root.get("confirmedRequests"), root.get("participantLimit"))
                    ));
        }

        Order order = cb.asc(root.get("eventDate"));
        if ("VIEWS".equals(sort)) {
            order = cb.desc(root.get("views"));
        }

        cbQuery.select(root).where(predicates.toArray(new Predicate[0])).orderBy(order);

        return entityManager.createQuery(cbQuery).setFirstResult(from).setMaxResults(size).getResultList();
    }
}