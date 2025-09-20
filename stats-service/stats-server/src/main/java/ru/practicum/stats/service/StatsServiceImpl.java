package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.formatter.TimeStampFormatter;
import ru.practicum.stats.interfaces.StatsService;
import ru.practicum.stats.model.EndpointHit;
import ru.practicum.stats.repository.JpaEndpointHit;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Реализация интерфейса {@link StatsService} для сбора статистики.
 * Этот класс отвечает за обработку логики сбора статистических данных.
 * Использует {@link JpaEndpointHit} для взаимодействия с уровнем данных по статистике обращений к эндпоинтам.
 */

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {
    private final JpaEndpointHit repository;

    @Override
    @Transactional
    public void saveHit(EndpointHitDto dto) {
        EndpointHit entity = new EndpointHit();
        entity.setApp(dto.getApp());
        entity.setUri(dto.getUri());
        entity.setIp(dto.getIp());
        entity.setTimestamp(TimeStampFormatter.parse(dto.getTimestamp()));

        repository.save(entity);
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        return unique
                ? repository.findUniqueStats(start, end, uris)
                : repository.findStats(start, end, uris);
    }
}
