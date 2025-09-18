package ru.practicum.exploreWithMe.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exploreWithMe.interfaces.StatisticCollector;
import ru.practicum.exploreWithMe.repository.JpaEndpointHit;

/**
 * Реализация интерфейса {@link StatisticCollector} для сбора статистики.
 * Этот класс отвечает за обработку логики сбора статистических данных.
 * Использует {@link JpaEndpointHit} для взаимодействия с уровнем данных по статистике обращений к эндпоинтам.
 */

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticCollectorImpl implements StatisticCollector {
    private final JpaEndpointHit endpointHit;

    @Override
    public boolean createStats() {
        return false;
    }
}
