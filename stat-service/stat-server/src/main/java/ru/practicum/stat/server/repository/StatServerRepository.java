package ru.practicum.stat.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.stat.common.dto.ViewStatisticDto;
import ru.practicum.stat.server.model.RecordStatistic;

import java.time.LocalDateTime;
import java.util.List;

public interface StatServerRepository extends JpaRepository<RecordStatistic, Long> {

    @Query("SELECT new ru.practicum.stat.common.dto.ViewStatisticDto(s.appName, s.uri, COUNT(s.ip)) " +
            "FROM RecordStatistic s " +
            "WHERE s.timestamp >= ?1 AND s.timestamp < ?2 " +
            "GROUP BY s.appName, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<ViewStatisticDto> findAllStatistic(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.stat.common.dto.ViewStatisticDto(s.appName, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM RecordStatistic s " +
            "WHERE s.timestamp >= ?1 AND s.timestamp < ?2 " +
            "GROUP BY s.appName, s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC")
    List<ViewStatisticDto> findAllStatisticUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.stat.common.dto.ViewStatisticDto(s.appName, s.uri, COUNT(s.ip)) " +
            "FROM RecordStatistic s " +
            "WHERE s.timestamp >= ?1 AND s.timestamp < ?2 AND s.uri IN ?3 " +
            "GROUP BY s.appName, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<ViewStatisticDto> findAllStatisticForUriList(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.stat.common.dto.ViewStatisticDto(s.appName, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM RecordStatistic s " +
            "WHERE s.timestamp >= ?1 AND s.timestamp < ?2 AND s.uri IN ?3 " +
            "GROUP BY s.appName, s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC")
    List<ViewStatisticDto> findAllStatisticForUriListAndUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);
}
