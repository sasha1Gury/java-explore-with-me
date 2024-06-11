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
                return statServerRepository.findAllStatistic(start, end);
            } else {
                return statServerRepository.findAllStatisticUniqueIp(start, end);
            }
        } else {
            if (!unique) {
                return statServerRepository.findAllStatisticForUriList(start, end, uris);
            } else {
                return statServerRepository.findAllStatisticForUriListAndUniqueIp(start, end, uris);
            }
        }
    }
}
