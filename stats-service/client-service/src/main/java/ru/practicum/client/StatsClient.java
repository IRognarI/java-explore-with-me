package ru.practicum.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.exploreWithMe.dto.EndpointHitDto;
import ru.practicum.exploreWithMe.dto.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StatsClient {

    private final RestTemplate rest;

    @Value("${stats.server.url:http://localhost:9090}")
    private String serverUrl;

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Сохраняем hit (POST /hit).
     */
    public void saveHit(EndpointHitDto hit) {
        rest.postForEntity(serverUrl + "/hit", hit, Void.class);
    }

    /**
     * Запрашиваем статистику (GET /stats).
     */
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end,
                                    List<String> uris, boolean unique) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serverUrl + "/stats")
                .queryParam("start", start.format(fmt))
                .queryParam("end", end.format(fmt))
                .queryParam("unique", unique);

        if (uris != null && !uris.isEmpty()) {
            uris.forEach(u -> builder.queryParam("uris", u));
        }

        String url = builder.encode().toUriString();

        ResponseEntity<List<ViewStats>> resp = rest.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        return resp.getBody();
    }
}
