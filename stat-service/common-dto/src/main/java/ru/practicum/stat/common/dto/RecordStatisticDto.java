package ru.practicum.stat.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordStatisticDto {
    @NotEmpty
    private String app;

    @NotEmpty
    private String uri;

    @NotEmpty
    private String ip;

    @NotEmpty
    private String timestamp;
}