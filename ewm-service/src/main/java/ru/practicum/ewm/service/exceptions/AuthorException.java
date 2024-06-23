package ru.practicum.ewm.service.exceptions;

public class AuthorException extends RuntimeException {
    public AuthorException() {
        super("Это проаво есть только у автора");
    }
}
