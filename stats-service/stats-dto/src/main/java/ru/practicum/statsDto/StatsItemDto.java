package ru.practicum.statsDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Объект передачи данных, представляющий информацию о статистике.
 * Реализует интерфейс {@link StatsItem} для предоставления конкретной реализации
 * передачи данных о статистике между различными слоями приложения.
 * Содержит информацию о названии приложения, URI и количестве просмотров.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatsItemDto implements StatsItem {

    private String app;
    private String uri;
    private long hits;

    public StatsItemDto(StatsItem si) {
        this.app = si.getApp();
        this.uri = si.getUri();
        this.hits = si.getHits();
    }
}