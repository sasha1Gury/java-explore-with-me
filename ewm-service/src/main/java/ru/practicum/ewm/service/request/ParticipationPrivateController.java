package ru.practicum.ewm.service.request;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.service.request.service.ParticipationService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ParticipationPrivateController {
    private final ParticipationService participationService;

    @GetMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestsByUserId(@Positive @PathVariable long userId) {
        log.info("Get all requests for user: userId = {}", userId);
        return participationService.getRequestsByUserId(userId);
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createParticipationRequest(@Positive @PathVariable long userId,
                                                              @Positive @RequestParam long eventId) {
        log.info("Create participation request: userId = {}, eventId = {}", userId, eventId);
        return participationService.createParticipationRequest(eventId, userId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelParticipationRequest(@Positive @PathVariable long userId,
                                                              @Positive @PathVariable long requestId) {
        log.info("Cancel participation request: userId = {}, requestId = {}", userId, requestId);
        return participationService.cancelParticipationRequest(requestId, userId);
    }
}