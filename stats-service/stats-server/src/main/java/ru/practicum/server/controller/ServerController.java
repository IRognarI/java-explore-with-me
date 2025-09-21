package ru.practicum.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.server.dto.requestDto.RequestDto;
import ru.practicum.server.interfaces.Server;

@RestController
@RequiredArgsConstructor
public class ServerController {

    private final Server server;

    @PostMapping("/hit")
    public ResponseEntity<String> addHit(@RequestBody RequestDto requestDto, HttpServletRequest request) {

        return server.addHit(requestDto, request) ?
                ResponseEntity.status(HttpStatus.CREATED).body("Информация сохранена") :
                ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body("Возникла ошибка при сохранении статистики");
    }
}
