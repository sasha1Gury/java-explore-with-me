package ru.practicum.ewm.service.exceptions;

public class CommentException extends RuntimeException {
    public CommentException(String message) {
        super("Невозможно добавить комментарий по причине - " + message);
    }
}
