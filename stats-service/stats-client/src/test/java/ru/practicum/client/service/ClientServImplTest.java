package ru.practicum.client.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.client.configuration.ClientConfig;
import ru.practicum.client.exception.LinksNotFoundException;
import ru.practicum.client.exception.ValidationException;
import ru.practicum.dto.requestDto.RequestDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ClientConfig clientConfig;

    @InjectMocks
    private ClientServImpl clientServ;

    private RequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestDto = RequestDto.builder()
                .app("test-app")
                .uri("/test-uri")
                .ip("127.0.0.1")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Test
    void responseFromEndpointHit_shouldReturnResponseEntity() {
        // Arrange
        String path = "/test-path";
        String url = "http://localhost:9090/test-path";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RequestDto> entity = new HttpEntity<>(requestDto, headers);
        ResponseEntity<String> expectedResponse = ResponseEntity.status(HttpStatus.CREATED).body("Информация сохранена");

        when(clientConfig.pathForEndPoint(Mockito.eq(path))).thenReturn(url);
        when(restTemplate.postForEntity(url, entity, String.class)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<String> result = clientServ.requestForEndPointWithPath_Hit(path, requestDto);

        // Assert
        assertEquals(expectedResponse, result);
        verify(clientConfig).pathForEndPoint(Mockito.eq(path));
        verify(restTemplate).postForEntity(url, entity, String.class);
    }

    @Test
    void requestForEndPointHit_shouldThrowException_whenPathIsNull() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> {
            clientServ.requestForEndPointWithPath_Hit(null, requestDto);
        });
    }

    @Test
    void requestForEndPointHit_shouldThrowException_whenPathIsEmpty() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> {
            clientServ.requestForEndPointWithPath_Hit("", requestDto);
        });
    }

    @Test
    void requestForEndPointHit_shouldThrowException_whenRequestDtoIsNull() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> {
            clientServ.requestForEndPointWithPath_Hit("/test-path", null);
        });
    }

    @Test
    void requestForEndPointWithPath_Stats_StartAfterEnd_ThrowsValidationException() {
        String path = "/stats";
        LocalDateTime start = LocalDateTime.of(2023, 12, 31, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 1, 0, 0);
        String[] uris = {"/events/1"};
        Boolean unique = true;

        ValidationException exception = assertThrows(ValidationException.class, () ->
                clientServ.requestForEndPointWithPath_Stats(path, start, end, uris, unique)
        );

        assertEquals("Начало диапазона поиска должно быть раньше конца", exception.getMessage());
    }

    @Test
    void requestForEndPointWithPath_Stats_EmptyUris_ThrowsLinksNotFoundException() {
        String path = "/stats";
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 31, 0, 0);
        String[] uris = {};
        Boolean unique = true;

        LinksNotFoundException exception = assertThrows(LinksNotFoundException.class, () ->
                clientServ.requestForEndPointWithPath_Stats(path, start, end, uris, unique)
        );

        assertEquals("Укажите ссылки для формирования статистики", exception.getMessage());
    }
}