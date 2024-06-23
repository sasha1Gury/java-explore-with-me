package ru.practicum.ewm.service.comments.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private long id;

    private Long event;

    private Long author;

    @NotEmpty
    @NotBlank
    @Size(min = 5, max = 10000, message = "Текст комментария должен быть от 5 до 10000 символов")
    private String text;

    private String created;
}