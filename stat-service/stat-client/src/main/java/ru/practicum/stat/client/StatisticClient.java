package ru.practicum.stat.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.stat.common.dto.RecordStatisticDto;
import ru.practicum.stat.common.dto.ViewStatisticDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class StatisticClient {
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final WebClient webClient;

    public StatisticClient(String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public void sendHit(RecordStatisticDto recordStatisticDto) {
        webClient.post()
                .uri("/hit")
                .bodyValue(recordStatisticDto)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnNext(aVoid -> log.info("Hit sent successfully"))
                .doOnError(throwable -> log.error("Error sending hit: {}", throwable.getMessage()))
                .block();
    }

    public List<ViewStatisticDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/stats")
                        .queryParam("start", start.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)))
                        .queryParam("end", end.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)))
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .bodyToFlux(ViewStatisticDto.class)
                .collectList()
                .block();
    }
}