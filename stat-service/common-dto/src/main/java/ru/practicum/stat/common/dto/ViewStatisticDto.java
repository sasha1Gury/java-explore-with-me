package ru.practicum.stat.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewStatisticDto {
    private String app;
    private String uri;
    private long hits;
}