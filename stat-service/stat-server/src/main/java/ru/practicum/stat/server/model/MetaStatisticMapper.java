package ru.practicum.stat.server.model;

import ru.practicum.stat.common.dto.RecordStatisticDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MetaStatisticMapper {

    public static RecordStatistic toEntity(RecordStatisticDto statRecordCreateDto) {
        return RecordStatistic.builder()
                .appName(statRecordCreateDto.getApp())
                .uri(statRecordCreateDto.getUri())
                .ip(statRecordCreateDto.getIp())
                .timestamp(LocalDateTime.parse(statRecordCreateDto.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}
