package ru.practicum.ewm.service.exceptions;

public class ParticipationRequestEventNotPublishedException extends RuntimeException {
    public ParticipationRequestEventNotPublishedException(String message) {
        super(message);
    }
}
