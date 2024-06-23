package ru.practicum.ewm.service.comments;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.comments.dto.CommentDto;
import ru.practicum.ewm.service.comments.service.CommentService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping()
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/users/{userId}/comments/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@RequestBody @Valid CommentDto commentDto,
                                    @PathVariable long userId,
                                    @PathVariable long eventId) {
        log.info("Create new comment for event: eventId = {}, userId = {}, commentDto = {}", userId, eventId, commentDto);
        return commentService.createComment(commentDto, userId, eventId);
    }

    @PatchMapping("/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(@RequestBody @Valid CommentDto updates,
                                    @PathVariable long userId,
                                    @PathVariable long commentId) {
        log.info("Update comment: user = {}, comment = {}, updateCommentDto = {}", userId, commentId, updates);
        return commentService.updateComment(updates, userId, commentId);
    }

    @GetMapping("/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto findCommentById(@PathVariable long userId,
                                      @PathVariable long commentId) {
        log.info("Get comment: userId = {}, commentId = {}", userId, commentId);
        return commentService.getCommentById(userId, commentId);
    }

    @GetMapping("/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> findAllCommentsByEvent(@PathVariable long eventId) {
        log.info("Get all comments for event: eventId = {}", eventId);
        return commentService.getAllCommentsByEvent(eventId);
    }

    @DeleteMapping("/events/{eventId}/comments/{userId}/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long eventId,
                              @PathVariable long userId,
                              @PathVariable long commentId) {
        log.info("Delete comment for event: eventId = {}, userId = {}, commentId = {}", eventId, userId, commentId);
        commentService.deleteEvent(userId, commentId, eventId);
    }

}
