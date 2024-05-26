package ru.practicum.stat.server.model;

import ru.practicum.stat.server.dto.MetaStatisticDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MetaStatisticMapper {
    public static MetaStatistic toEntity(MetaStatisticDto statRecordCreateDto) {
        if (statRecordCreateDto != null) {
            return MetaStatistic.builder()
                    .appName(statRecordCreateDto.getApp())
                    .uri(statRecordCreateDto.getUri())
                    .ip(statRecordCreateDto.getIp())
                    .timestamp(LocalDateTime.parse(statRecordCreateDto.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .build();
        } else {
            return null;
        }
    }
}
