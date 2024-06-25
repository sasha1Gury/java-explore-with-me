package ru.practicum.ewm.service.exceptions;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException() {
        super("Комментарий не найден");
    }
}
