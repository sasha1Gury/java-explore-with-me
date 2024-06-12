package ru.practicum.ewm.service.exceptions;

public class ParticipationRequestDuplicationException extends RuntimeException {
    public ParticipationRequestDuplicationException(String message) {
        super(message);
    }
}
