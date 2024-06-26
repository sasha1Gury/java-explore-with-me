package ru.practicum.ewm.service.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.service.category.dto.CategoryDto;
import ru.practicum.ewm.service.event.model.Location;
import ru.practicum.ewm.service.user.dto.UserShortDto;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {
    private long id;

    private String annotation;

    private CategoryDto category;

    private int confirmedRequests;

    private String createdOn;

    private String description;

    private String eventDate;

    private UserShortDto initiator;

    private Location location;

    private boolean paid;

    private int participantLimit;

    private String publishedOn;

    private boolean requestModeration;

    private String state;

    private String title;

    private int views;
}