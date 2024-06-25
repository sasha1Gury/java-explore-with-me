package ru.practicum.ewm.service.errorHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.service.exceptions.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);


    @ExceptionHandler({ValidationException.class,
            NegativeParticipantsException.class,
            EndBeforeStartException.class,
            DateException.class,
            ValidateException.class,
            CompilationException.class,
            CommentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final RuntimeException e) {
        log.info(e.getMessage());
        return ErrorResponse.builder()
                .status("BAD_REQUEST")
                .reason("error")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                .build();
    }

    @ExceptionHandler({CategoryNotFoundException.class,
            CompilationNotFoundException.class,
            EventNotFoundException.class,
            UserNotFoundException.class,
            ParticipationRequestNotFoundException.class,
            CommentNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final RuntimeException e) {
        log.info(e.getMessage());
        return ErrorResponse.builder()
                .status("NOT_FOUND")
                .reason("error")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                .build();
    }

    @ExceptionHandler({CategoryHaveLinkedEventsException.class,
            CategoryNameNotUniqueException.class,
            EventUpdateException.class,
            ParticipationRequestDuplicationException.class,
            ParticipationRequestEventNotPublishedException.class,
            ParticipationRequestInitiatorException.class,
            ParticipationRequestInvalidStateException.class,
            ParticipationRequestLimitException.class,
            ParticipationRequestLimitReachedException.class,
            UserEmailNotUniqueException.class,
            AuthorException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserEmailNotUniqueException(final RuntimeException e) {
        log.info(e.getMessage());
        return ErrorResponse.builder()
                .status("CONFLICT")
                .reason("error")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                .build();
    }

    @ExceptionHandler({IllegalArgumentException.class,
            EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse serverExceptionHandler(RuntimeException e) {
        log.info(e.getMessage(), e);
        return ErrorResponse.builder()
                .status("NOT_FOUND")
                .reason("Внутренняя ошибка сервера")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                .build();
    }
}