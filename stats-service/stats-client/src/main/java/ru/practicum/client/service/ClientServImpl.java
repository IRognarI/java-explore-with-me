package ru.practicum.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.client.configuration.ClientConfig;
import ru.practicum.client.exception.LinksNotFoundException;
import ru.practicum.client.exception.ValidationException;
import ru.practicum.client.interfaces.ClientService;
import ru.practicum.server.dto.requestDto.RequestDto;
import ru.practicum.server.dto.responceDto.ViewStats;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ClientServImpl implements ClientService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ClientConfig clientConfig;

    @Autowired
    private HttpHeaders headers;

    @Override
    public ResponseEntity<String> requestForEndPointWithPath_Hit(String path, RequestDto requestDto) {

        if (path == null || path.isEmpty()) {
            throw new ValidationException("Укажите путь для создания запроса");
        }

        if (requestDto == null) {
            throw new ValidationException("Не достаточно данных для создания запроса");
        }

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RequestDto> entity = new HttpEntity<>(requestDto, headers);

        return restTemplate.postForEntity(clientConfig.pathForEndPoint(path), entity, String.class);
    }

    @Override
    public ResponseEntity<List<ViewStats>> requestForEndPointWithPath_Stats(
            String path,
            LocalDateTime start,
            LocalDateTime end,
            String[] uris,
            Boolean unique) {

        if (start.isAfter(end)) {
            throw new ValidationException("Начало диапазона поиска должно быть раньше конца");
        }

        if (uris.length == 0) {
            throw new LinksNotFoundException("Укажите ссылки для формирования статистики");
        }

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
        builder.path(clientConfig.pathForEndPoint(path))
                .queryParam("start", start)
                .queryParam("end", end);

        for (String val : uris) {
            builder.queryParam("uris", val);
        }

        builder.queryParam("unique", unique).encode(StandardCharsets.UTF_8);

        String finishPath = builder.toUriString();

        return restTemplate.exchange(
                finishPath,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<ViewStats>>() {
                }
        );
    }
}