package ru.practicum.ewm.service.comments.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.service.comments.dto.CommentDto;
import ru.practicum.ewm.service.comments.model.Comment;
import ru.practicum.ewm.service.comments.model.CommentMapper;
import ru.practicum.ewm.service.comments.repository.CommentRepository;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.event.model.EventState;
import ru.practicum.ewm.service.event.service.EventService;
import ru.practicum.ewm.service.exceptions.AuthorException;
import ru.practicum.ewm.service.exceptions.CommentException;
import ru.practicum.ewm.service.exceptions.CommentNotFoundException;
import ru.practicum.ewm.service.user.model.User;
import ru.practicum.ewm.service.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final EventService eventService;
    private final UserService userService;
    private final ModelMapper mapper;

    public CommentDto createComment(CommentDto commentDto, long userId, long eventId) {
        Event event = eventService.getEventById(eventId);

        if (event.getState() != EventState.PUBLISHED) {
            throw new CommentException("Собитие не опубликовано");
        }

        Comment comment = Comment.builder()
                .event(event)
                .author(userService.getUserById(userId))
                .text(commentDto.getText())
                .created(LocalDateTime.now())
                .build();

        return mapper.map(commentRepository.save(comment), CommentDto.class);
    }

    public CommentDto getCommentById(long userId, long commentId) {
        User author = userService.getUserById(userId);
        Comment model = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);

        if (author.getId() != model.getAuthor().getId()) {
            throw new AuthorException();
        }

        return mapper.map(model, CommentDto.class);
    }

    public CommentDto updateComment(CommentDto updates, long userId, long commentId) {
        User author = userService.getUserById(userId);

        Comment model = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
        if (author.getId() != model.getAuthor().getId()) {
            throw new AuthorException();
        }
        CommentMapper.updateEntity(updates, model);
        return mapper.map(commentRepository.save(model), CommentDto.class);

    }


    public List<CommentDto> getAllCommentsByEvent(long eventId) {
        Event event = eventService.getEventById(eventId);
        return commentRepository.findAllByEvent_Id(eventId).stream()
                .map(comment -> mapper.map(comment, CommentDto.class))
                .collect(Collectors.toList());
    }

    public void deleteEvent(long userId, long commentId, long eventId) {
        User author = userService.getUserById(userId);
        CommentDto commentDto = getCommentById(userId, commentId);
        if (commentDto.getEvent() != eventId || commentDto.getAuthor() != userId) {
            throw new CommentNotFoundException();
        }

        commentRepository.delete(mapper.map(commentDto, Comment.class));
    }
}
