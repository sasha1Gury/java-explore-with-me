package ru.practicum.ewm.service.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.service.compilation.model.Compilation;

public interface CompilationRepository extends JpaRepository<Long, Compilation> {
}
