package ru.practicum.ewm.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.event.dto.EventFullDto;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.model.EventDtoMapper;
import ru.practicum.ewm.service.event.service.EventService;
import ru.practicum.ewm.service.exceptions.EndBeforeStartException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.service.errorHandler.ErrorHandler.DATE_TIME_FORMAT;

@RestController
@RequestMapping(path = "/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventPublicController {
    private final EventService eventService;

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventByIdPublic(@PathVariable long eventId, HttpServletRequest request) {
        String remoteIp = request.getRemoteAddr();
        String requestUri = request.getRequestURI();

        log.info("Public get event: eventId = {}, remoteIp = {}, requestUri = {}", eventId, remoteIp, requestUri);
        return eventService.getEventByIdPublic(eventId, requestUri, remoteIp);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAllEventsPublic(@RequestParam(required = false) String text,
                                                  @RequestParam(name = "categories", required = false) List<Integer> categoriesIdList,
                                                  @RequestParam(required = false) Boolean paid,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeStart,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeEnd,
                                                  @RequestParam(required = false) Boolean onlyAvailable,
                                                  @RequestParam(required = false) String sort,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                  @Positive @RequestParam(defaultValue = "10") int size,
                                                  HttpServletRequest request) {
        String remoteIp = request.getRemoteAddr();
        String requestUri = request.getRequestURI();

        if (rangeEnd != null && rangeStart != null) {
            if (rangeEnd.isBefore(rangeStart)) {
                throw new EndBeforeStartException("Ошибка в датах");
            }
        }

        log.info("Public get all events: text = {}, categoriesIdList = {}, paid = {}, rangeStart = {}, rangeEnd = {}, onlyAvailable = {}, sort = {}, from = {}, size = {}",
                text, categoriesIdList, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        log.info("Public get all events: remoteIp = {}, requestUri = {}", remoteIp, requestUri);

        return EventDtoMapper.toEventShortDtoList(
                eventService.getAllEventsPublic(text, categoriesIdList, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, requestUri, remoteIp)
        );
    }
}
