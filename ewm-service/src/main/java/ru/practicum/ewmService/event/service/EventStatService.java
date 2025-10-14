package ru.practicum.ewmService.event.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.statsClient.StatsClient;

@Slf4j
@Service
public class EventStatService extends StatsClient {

    public EventStatService(@Value("${stats.server.url:http://stats-server:9090}") String serverUrl) {
        super(serverUrl);
    }
}