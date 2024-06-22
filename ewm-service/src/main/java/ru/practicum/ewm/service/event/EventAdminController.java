package ru.practicum.ewm.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.UpdateEventRequest;
import ru.practicum.ewm.service.event.model.EventState;
import ru.practicum.ewm.service.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.service.errorHandler.ErrorHandler.DATE_TIME_FORMAT;

@RestController
@RequestMapping(path = "/admin/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventAdminController {
    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getAllEvents(@RequestParam(name = "users", defaultValue = "") List<Integer> usersIdList,
                                           @RequestParam(name = "states", defaultValue = "") List<EventState> states,
                                           @RequestParam(name = "categories", defaultValue = "") List<Integer> categoriesIdList,
                                           @RequestParam(defaultValue = "")
                                               @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeStart,
                                           @RequestParam(defaultValue = "")
                                               @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeEnd,
                                           @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                           @Positive @RequestParam(defaultValue = "10") int size) {

        log.info("Admin get all events: users = {}, states = {}, categories = {}, rangeStart = {}, rangeEnd = {}, from = {}, size = {}",
                usersIdList, states, categoriesIdList, rangeStart, rangeEnd, from, size);

        return eventService.getAllEventsAdmin(usersIdList, states, categoriesIdList, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto update(@Valid @RequestBody UpdateEventRequest updateRequest, @Positive @PathVariable long eventId) {
        log.info("Admin update event: {}, eventId = {}", updateRequest, eventId);
        return eventService.updateEventByAdmin(updateRequest, eventId);
    }
}
