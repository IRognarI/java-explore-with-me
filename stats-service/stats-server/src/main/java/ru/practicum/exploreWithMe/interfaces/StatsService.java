package ru.practicum.exploreWithMe.interfaces;

import ru.practicum.exploreWithMe.dto.EndpointHitDto;
import ru.practicum.exploreWithMe.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    public void saveHit(EndpointHitDto dto);

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end,
                                    List<String> uris, boolean unique);
}
