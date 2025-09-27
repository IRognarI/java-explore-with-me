package ru.practicum.server.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.requestDto.RequestDto;
import ru.practicum.dto.responseDto.ViewStats;
import ru.practicum.server.model.EndpointHit;

/**
 * Утилитарный класс для преобразования между объектами {@link EndpointHit} и {@link ViewStats}.
 * Предоставляет методы для преобразования {@link EndpointHit} в {@link ViewStats} и наоборот.
 */

@UtilityClass
public class Mapper {
    public static ViewStats toViewStats(EndpointHit endpointHit) {
        return ViewStats.builder()
                .app(endpointHit.getApp())
                .uri(endpointHit.getUri())
                .build();
    }

    public static EndpointHit toEndpointHit(ViewStats viewStats) {
        return EndpointHit.builder()
                .app(viewStats.getApp())
                .uri(viewStats.getUri())
                .build();
    }

    public static EndpointHit toEntityFromRequestDto(RequestDto requestDto) {
        return EndpointHit.builder()
                .app(requestDto.getApp())
                .uri(requestDto.getUri())
                .ip(requestDto.getIp())
                .timestamp(requestDto.getTimestamp())
                .build();
    }
}
