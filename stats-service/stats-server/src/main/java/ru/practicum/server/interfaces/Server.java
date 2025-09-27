package ru.practicum.server.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.dto.requestDto.RequestDto;
import ru.practicum.dto.responseDto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface Server {
    boolean addHit(RequestDto requestDto, HttpServletRequest request);

    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique);
}
