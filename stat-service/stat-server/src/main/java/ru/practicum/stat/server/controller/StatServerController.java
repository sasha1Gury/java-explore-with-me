package ru.practicum.stat.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stat.common.dto.ViewStatisticDto;
import ru.practicum.stat.common.dto.RecordStatisticDto;
import ru.practicum.stat.server.service.StatServerService;

import javax.xml.bind.ValidationException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
public class StatServerController {
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final StatServerService statServerService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveStatistic(@RequestBody @Validated RecordStatisticDto recordStatisticDto) {
        log.info("New hit: {}", recordStatisticDto);
        statServerService.save(recordStatisticDto);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStatisticDto> getStatistic(@RequestParam @DateTimeFormat(pattern = DATE_TIME_FORMAT)LocalDateTime start,
                                                @RequestParam @DateTimeFormat(pattern = DATE_TIME_FORMAT)LocalDateTime end,
                                                @RequestParam(defaultValue = "") List<String> uris,
                                                @RequestParam(defaultValue = "false") boolean unique) throws ValidationException {
        if (end.isBefore(start)) {
            throw new ValidationException("Время начала интервала должно быть меньше времени конца");
        }
        List<ViewStatisticDto> statsList = statServerService.getStatistic(start, end, uris, unique);
        log.info("Get stats from {} to {}, uris: {}, unique {}. Found {} records", start, end, uris, unique, statsList.size());
        return statsList;
    }
}
