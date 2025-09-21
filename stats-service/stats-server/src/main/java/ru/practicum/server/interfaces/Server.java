package ru.practicum.server.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.server.dto.requestDto.RequestDto;

public interface Server {
    boolean addHit(RequestDto requestDto, HttpServletRequest request);
}
