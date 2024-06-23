package ru.practicum.ewm.service.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.compilation.dto.CompilationDto;
import ru.practicum.ewm.service.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CompilationPublicController {
    private final CompilationService compilationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> getAllCompilations(@RequestParam(required = false) Boolean pinned,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                   @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Public get all compilations: pinned = {}, from = {}, size = {}", pinned, from, size);
        return compilationService.getAllCompilations(pinned, from, size);
    }

    @GetMapping("/{compilationId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto getCompilationById(@Positive @PathVariable long compilationId) {
        log.info("Public get compilation: compilationId = {}", compilationId);
        return compilationService.getCompilationById(compilationId);
    }
}