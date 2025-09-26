package ru.practicum.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.requestDto.RequestDto;
import ru.practicum.dto.responseDto.ViewStats;
import ru.practicum.server.exception.ErrorSavingStatistics;
import ru.practicum.server.interfaces.Server;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ServerController {
    private static final Logger LOG = LoggerFactory.getLogger(ServerController.class);

    private final Server server;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public boolean addHit(@RequestBody @Valid RequestDto requestDto, HttpServletRequest request) {

        LOG.info("Получен запрос с телом: {}", requestDto);

        boolean result = server.addHit(requestDto, request);

        if (!result) {
            LOG.error("Статистика не была сохранена в базу");
            throw new ErrorSavingStatistics("Статистика не была сохранена в базу");
        }

        return result;
    }

    @GetMapping("/stats")
    List<ViewStats> getStats(
            @RequestParam(name = "start")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime start,

            @RequestParam(name = "end")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime end,

            @RequestParam(name = "uris", required = false)
            String[] uris,

            @RequestParam(name = "unique", required = false)
            Boolean unique) {

        LOG.info("Получен GET /stats с параметрами: start={}, end={}, uris={}, unique={}", start, end, uris, unique);

        Boolean uniqueValue = unique != null ? unique : false;

        return server.getStats(start, end, uris, uniqueValue);
    }
}
