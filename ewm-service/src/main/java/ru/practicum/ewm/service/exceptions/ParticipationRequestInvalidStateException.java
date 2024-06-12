package ru.practicum.ewm.service.exceptions;

public class ParticipationRequestInvalidStateException extends RuntimeException {
    public ParticipationRequestInvalidStateException(String message) {
        super(message);
    }
}
