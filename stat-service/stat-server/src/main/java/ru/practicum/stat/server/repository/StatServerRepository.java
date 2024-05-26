package ru.practicum.stat.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.stat.server.model.MetaStatistic;

public interface StatServerRepository extends JpaRepository<MetaStatistic, Long> {
}
