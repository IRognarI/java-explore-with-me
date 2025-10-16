package ru.practicum.statsServer.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.statsDto.NewHitDto;
import ru.practicum.statsDto.StatsItemDto;
import ru.practicum.statsServer.exceptions.IsBadRequestException;
import ru.practicum.statsServer.interfaces.HitService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Контроллер для обработки запросов статистики посещений.
 * Предоставляет эндпоинты для добавления хитов и получения статистики.
 */

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping()
public class HitController {

    private final HitService hitService;
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addHit(@Valid @RequestBody NewHitDto dto) {

        log.info("Add hit POST request: {}", dto);
        hitService.addHit(dto);
    }

    @GetMapping("/stats")
    public List<StatsItemDto> getStats(
            @RequestParam @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(required = false, defaultValue = "false") boolean unique) {

        if (start.isAfter(end)) {
            throw new IsBadRequestException("Start date must be before end date");
        }

        log.info("Get Stats request: start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return hitService.getStats(start, end, uris, unique);
    }
}