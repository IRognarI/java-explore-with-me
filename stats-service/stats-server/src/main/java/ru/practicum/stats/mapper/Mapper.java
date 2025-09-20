package ru.practicum.stats.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.model.EndpointHit;

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
}
