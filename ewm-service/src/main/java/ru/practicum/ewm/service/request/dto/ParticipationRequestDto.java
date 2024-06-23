package ru.practicum.ewm.service.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDto {
    private long id;

    private LocalDateTime created;

    private Long event;

    private Long requester;

    private String status;
}