package ru.practicum.stat.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.stat.server.dto.MetaStatisticDto;
import ru.practicum.stat.server.service.StatServerService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StatServerController {
    private final StatServerService statServerService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void RequestEndpoint(@RequestBody @Validated MetaStatisticDto metaStatisticDto) {
        log.info("New hit: {}", metaStatisticDto);
        statServerService.save(metaStatisticDto);
    }
}
