package ru.practicum.ewm.service.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.service.event.model.Location;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventRequest {
    private String annotation;

    private Long category;

    private String description;

    private String eventDate;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private String stateAction;

    private String title;
}