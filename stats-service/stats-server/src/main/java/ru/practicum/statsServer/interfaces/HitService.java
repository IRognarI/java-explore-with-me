package ru.practicum.statsServer.interfaces;

import ru.practicum.statsDto.NewHitDto;
import ru.practicum.statsDto.StatsItemDto;

import java.time.LocalDateTime;
import java.util.List;

public interface HitService {

    void addHit(NewHitDto dto);

    List<StatsItemDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}