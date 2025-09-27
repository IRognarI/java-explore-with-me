package ru.practicum.client.interfaces;

import org.springframework.http.ResponseEntity;
import ru.practicum.dto.requestDto.RequestDto;
import ru.practicum.dto.responseDto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface ClientService {
    ResponseEntity<String> requestForEndPointWithPath_Hit(String path, RequestDto requestDto);

    ResponseEntity<List<ViewStats>> requestForEndPointWithPath_Stats(
            String path,
            LocalDateTime start,
            LocalDateTime end,
            String[] uris,
            Boolean unique);
}