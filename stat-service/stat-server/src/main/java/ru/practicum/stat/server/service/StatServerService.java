package ru.practicum.stat.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stat.common.dto.RecordStatisticDto;
import ru.practicum.stat.common.dto.ViewStatisticDto;
import ru.practicum.stat.server.model.RecordStatistic;
import ru.practicum.stat.server.model.MetaStatisticMapper;
import ru.practicum.stat.server.repository.StatServerRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatServerService {
    private final StatServerRepository statServerRepository;

    public void save(RecordStatisticDto recordStatisticDto) {
        RecordStatistic recordStatistic = MetaStatisticMapper.toEntity(recordStatisticDto);
        statServerRepository.save(recordStatistic);
    }

    public List<ViewStatisticDto> getStatistic(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (uris.isEmpty()) {
            if (!unique) {
                return statServerRepository.findStatistic(start, end);
            } else {
                return statServerRepository.findStatisticUniqueIp(start, end);
            }
        } else {
            if (!unique) {
                return statServerRepository.findStatisticForUriList(start, end, uris);
            } else {
                return statServerRepository.findStatisticForUriListAndUniqueIp(start, end, uris);
            }
        }
    }
}
