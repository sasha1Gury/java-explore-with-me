package ru.practicum.ewm.service.exceptions;

public class EndBeforeStartException extends RuntimeException {
    public EndBeforeStartException(String message) {
        super(message);
    }
}
