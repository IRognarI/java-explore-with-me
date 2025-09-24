package ru.practicum.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.server.dto.requestDto.RequestDto;
import ru.practicum.server.dto.requestDto.ViewStats;
import ru.practicum.server.interfaces.Server;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class ServerController {

    private final Server server;

    @PostMapping("/hit")
    public ResponseEntity<String> addHit(@RequestBody @Valid RequestDto requestDto, HttpServletRequest request) {

        return server.addHit(requestDto, request) ?
                ResponseEntity.status(HttpStatus.CREATED).body("Информация сохранена") :
                ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body("Возникла ошибка при сохранении статистики");
    }

    @GetMapping("/stats")
    ResponseEntity<List<ViewStats>> getStats(
            @RequestParam(name = "start")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime start,

            @RequestParam(name = "end")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime end,

            @RequestParam(name = "uris")
            @Size(min = 1, message = "Укажите хотя бы 1 ссылку для получения статистики")
            String[] uris,

            @RequestParam(name = "unique", defaultValue = "false")
            Boolean unique) {

        List<ViewStats> statsList = server.getStats(start, end, uris, unique);

        return !statsList.isEmpty() ?
                ResponseEntity.ok(statsList) :
                ResponseEntity.status(HttpStatus.NO_CONTENT).body(statsList);
    }
}
