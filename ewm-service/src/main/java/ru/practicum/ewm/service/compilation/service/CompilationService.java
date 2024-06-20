package ru.practicum.ewm.service.compilation.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.service.compilation.dto.CompilationDto;
import ru.practicum.ewm.service.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.service.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.service.compilation.model.Compilation;
import ru.practicum.ewm.service.compilation.repository.CompilationRepository;
import ru.practicum.ewm.service.event.repository.EventRepository;
import ru.practicum.ewm.service.exceptions.CompilationException;
import ru.practicum.ewm.service.exceptions.CompilationNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final ModelMapper mapper;

    public CompilationDto getCompilationById(long compilationId) {
        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new CompilationNotFoundException("Компиляция " + compilationId + " не найдена"));

        return mapper.map(compilation, CompilationDto.class);
    }

    public List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size) {
        Pageable page = PageRequest.of(from / size, size);
        if (pinned != null) {
            return compilationRepository.findByPinned(pinned, page)
                    .stream()
                    .map(compilation -> mapper.map(compilation, CompilationDto.class))
                    .collect(Collectors.toList());
        } else {
            return compilationRepository.findAll(page).getContent()
                    .stream()
                    .map(compilation -> mapper.map(compilation, CompilationDto.class))
                    .collect(Collectors.toList());
        }
    }

    public CompilationDto create(NewCompilationDto newCompilationDto) {

        Compilation compilation = Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.getPinned())
                .build();
        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            compilation.setEvents(Set.copyOf(eventRepository.findByIdIn(List.copyOf(newCompilationDto.getEvents()))));
        }

        validateCompilation(compilation);

        return mapper.map(compilationRepository.save(compilation), CompilationDto.class);
    }

    public void validateCompilation(Compilation compilation) {

        if (compilation.getPinned() == null) compilation.setPinned(false);
        if (compilation.getTitle().trim().isEmpty()) throw new CompilationException("Поле title не должен быть пустым");
        if (compilation.getTitle().length() > 50) throw new CompilationException("Поле title не должно превышать по длине 50 символов");
    }

    public CompilationDto update(UpdateCompilationRequest updateRequest, long compilationId) {
        CompilationDto compilationDto = getCompilationById(compilationId);
        Compilation compilation = mapper.map(compilationDto, Compilation.class);
        if (updateRequest.getEvents() != null) {
            if (updateRequest.getEvents().isEmpty()) {
                compilation.setEvents(new HashSet<>());
            } else {
                compilation.setEvents(eventRepository.findByIdIn(List.copyOf(updateRequest.getEvents())));
            }
        }

        if (updateRequest.getPinned() != null) compilation.setPinned(updateRequest.getPinned());
        if (updateRequest.getTitle() != null) compilation.setTitle(updateRequest.getTitle());

        validateCompilation(compilation);

        return mapper.map(compilationRepository.save(compilation), CompilationDto.class);
    }

    public void delete(long compilationId) {
        CompilationDto compilationDto = getCompilationById(compilationId);

        compilationRepository.delete(mapper.map(compilationDto, Compilation.class));
    }
}