package ru.practicum.statsDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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