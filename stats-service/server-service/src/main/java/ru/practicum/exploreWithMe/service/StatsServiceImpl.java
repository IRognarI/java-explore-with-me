package ru.practicum.exploreWithMe.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.dto.EndpointHitDto;
import ru.practicum.exploreWithMe.dto.ViewStats;
import ru.practicum.exploreWithMe.formatter.TimeStampFormatter;
import ru.practicum.exploreWithMe.interfaces.StatsService;
import ru.practicum.exploreWithMe.model.EndpointHit;
import ru.practicum.exploreWithMe.repository.JpaEndpointHit;

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
