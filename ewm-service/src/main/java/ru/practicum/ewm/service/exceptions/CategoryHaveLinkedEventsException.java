package ru.practicum.ewm.service.exceptions;

//ожидаемый код 409 CONFLICT

public class CategoryHaveLinkedEventsException extends RuntimeException {
    public CategoryHaveLinkedEventsException(String message) {
        super(message);
    }
}
