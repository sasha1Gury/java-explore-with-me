package ru.practicum.ewm.service.exceptions;

public class ParticipationRequestLimitReachedException extends RuntimeException {
    public ParticipationRequestLimitReachedException(String message) {
        super(message);
    }
}
