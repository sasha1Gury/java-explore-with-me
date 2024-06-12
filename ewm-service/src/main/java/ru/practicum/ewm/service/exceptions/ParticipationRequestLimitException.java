package ru.practicum.ewm.service.exceptions;

public class ParticipationRequestLimitException extends RuntimeException {
    public ParticipationRequestLimitException(String message) {
        super(message);
    }
}
