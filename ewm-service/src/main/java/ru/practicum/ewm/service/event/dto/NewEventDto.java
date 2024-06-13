package ru.practicum.ewm.service.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.service.event.model.Location;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {
    @NotEmpty
    @Size(min = 20, max = 2000)
    private String annotation;

    @Positive
    private long category;

    @NotEmpty
    @Size(min = 20, max = 7000)
    private String description;

    @NotEmpty
    private String eventDate;

    @NotNull
    private Location location;

    private boolean paid = false;

    private int participantLimit = 0;

    private boolean requestModeration = true;

    @NotEmpty
    @Size(min = 3, max = 120)
    private String title;
}