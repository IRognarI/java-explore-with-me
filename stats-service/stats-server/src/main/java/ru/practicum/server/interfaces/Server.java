package ru.practicum.server.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.server.dto.requestDto.RequestDto;
import ru.practicum.server.dto.requestDto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface Server {
    boolean addHit(RequestDto requestDto, HttpServletRequest request);

    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique);
}
