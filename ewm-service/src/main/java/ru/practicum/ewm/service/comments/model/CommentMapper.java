package ru.practicum.ewm.service.comments.model;

import ru.practicum.ewm.service.comments.dto.CommentDto;

public class CommentMapper {
    public static void updateEntity(CommentDto updates, Comment model) {
        model.setText(updates.getText());
    }
}
