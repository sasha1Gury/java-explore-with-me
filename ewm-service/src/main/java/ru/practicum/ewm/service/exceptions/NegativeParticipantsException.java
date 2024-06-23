package ru.practicum.ewm.service.exceptions;

public class NegativeParticipantsException extends RuntimeException {
    public NegativeParticipantsException(String message) {
        super(message);
    }
}
