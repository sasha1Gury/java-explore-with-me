package ru.practicum.ewm.service.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.service.category.dto.CategoryDto;
import ru.practicum.ewm.service.user.dto.UserShortDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDto {
    private long id;

    private String annotation;

    private CategoryDto category;

    private int confirmedRequests;

    private String eventDate;

    private UserShortDto initiator;

    private boolean paid;

    private String title;

    private int views;
}