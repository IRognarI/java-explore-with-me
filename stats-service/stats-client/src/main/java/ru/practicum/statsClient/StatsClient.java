package ru.practicum.statsClient;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.statsDto.NewHitDto;
import ru.practicum.statsDto.StatsItemDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Slf4j
public class StatsClient {

    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final String serverUrl;
    private final RestTemplate rest;

    public StatsClient(String serverUrl) {

        this.serverUrl = serverUrl;
        this.rest = new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build();
    }

    protected <R> ResponseEntity<R> get(String path, Map<String, Object> parameters, Class<R> responseType) {
        return makeAndSendRequest(HttpMethod.GET, path, parameters, null, responseType);
    }

    protected <T> void post(String path, T body) {
        makeAndSendRequest(HttpMethod.POST, path, null, body, Object.class);
    }

    private <T, R> ResponseEntity<R> makeAndSendRequest(
            HttpMethod method, String path, @Nullable Map<String, Object> parameters, @Nullable T body, Class<R> responseType) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<T> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<R> response;
        if (parameters != null) {
            response = rest.exchange(path, method, requestEntity, responseType, parameters);
        } else {
            response = rest.exchange(path, method, requestEntity, responseType);
        }
        return prepareResponse(response);
    }

    private static <R> ResponseEntity<R> prepareResponse(ResponseEntity<R> response) {

        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }


    public void addHit(@NonNull String app,
                       @NonNull String uri,
                       @NonNull String ip,
                       @NonNull LocalDateTime time) {

        NewHitDto newHitDto = new NewHitDto(app, uri, ip, time);
        String path = "/hit";
        log.info("Post hit {} to server endpoint {}{}", newHitDto, serverUrl, path);
        try {
            post(path, newHitDto);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to send hit to stats server", e);
        }
    }

    public List<StatsItemDto> getStats(
            @NonNull LocalDateTime start,
            @NonNull LocalDateTime end,
            @NonNull List<String> uris,
            boolean unique) {

        log.info("Get stats for uris {} from {}/stats", uris, serverUrl);
        try {
            ResponseEntity<StatsItemDto[]> responseEntity = get(
                    "/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                    Map.of(
                            "start", formatter.format(start),
                            "end", formatter.format(end),
                            "uris", String.join(",", uris),
                            "unique", unique),
                    StatsItemDto[].class);
            StatsItemDto[] result = responseEntity.getBody();
            if (result == null) {
                throw new RuntimeException("getStats returned nullable body");
            }
            return Arrays.asList(result);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to get stats from stats server", e);
        }
    }
}