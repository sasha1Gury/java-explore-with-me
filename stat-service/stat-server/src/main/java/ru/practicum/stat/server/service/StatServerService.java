package ru.practicum.stat.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stat.server.dto.MetaStatisticDto;
import ru.practicum.stat.server.model.MetaStatistic;
import ru.practicum.stat.server.model.MetaStatisticMapper;
import ru.practicum.stat.server.repository.StatServerRepository;

@Service
@RequiredArgsConstructor
public class StatServerService {
    private final StatServerRepository statServerRepository;

    public MetaStatistic save(MetaStatisticDto metaStatisticDto) {
        MetaStatistic metaStatistic = MetaStatisticMapper.toEntity(metaStatisticDto);
        return statServerRepository.save(metaStatistic);
    }

}
