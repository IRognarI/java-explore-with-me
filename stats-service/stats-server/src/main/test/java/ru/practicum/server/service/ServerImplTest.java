package ru.practicum.server.service;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.dto.requestDto.RequestDto;
import ru.practicum.dto.responseDto.ViewStats;
import ru.practicum.server.exception.ErrorGettingAnIpAddress;
import ru.practicum.server.exception.ValidationException;
import ru.practicum.server.model.EndpointHit;
import ru.practicum.server.repository.JpaEndpointHit;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ServerImplTest {

    @Mock
    private JpaEndpointHit repository;

    @Mock
    private HttpServletRequest request;


    @InjectMocks
    private ServerImpl server;

    private HttpServletRequest http;
    private HttpServletRequest http_2;
    private RequestDto requestDto;
    private RequestDto requestDto_2;
    private EndpointHit endpointHit;
    private List<ViewStats> viewStatsList = new ArrayList<>();

    @BeforeEach
    public void setup() {
        http = Mockito.mock(HttpServletRequest.class);
        http_2 = Mockito.mock(HttpServletRequest.class);
        requestDto = RequestDto.builder()
                .uri("/ewent/1")
                .app("ewm-service")
                .ip("127.0.0.1")
                .timestamp(LocalDateTime.now())
                .build();


        endpointHit = EndpointHit.builder()
                .id(1L)
                .uri(requestDto.getUri())
                .app(requestDto.getApp())
                .ip(requestDto.getIp())
                .timestamp(requestDto.getTimestamp())
                .build();

        viewStatsList.add(ViewStats.builder()
                .app("ewm")
                .uri("/stats/1")
                .hits(2L)
                .build());

        viewStatsList.add(ViewStats.builder()
                .app("ewm_2")
                .uri("/stats/2")
                .hits(4L)
                .build());

        viewStatsList.add(ViewStats.builder()
                .app("ewm_3")
                .uri("/stats/3")
                .hits(9L)
                .build());
    }

    @Test
    public void addHit_Is_True() {

        when(http.getLocalAddr()).thenReturn("127.0.0.1");
        when(http.getRequestURI()).thenReturn("/ewent/1");

        when(repository.save(any(EndpointHit.class)))
                .thenReturn(endpointHit);

        boolean result = server.addHit(requestDto, http);

        assertTrue(result, "Метод вернул false");

        Mockito.verify(repository, times(1)).save(any(EndpointHit.class));
    }

    @Test
    public void addHit_RequestDto_Is_null() {
        assertThrows(ValidationException.class, () -> server.addHit(requestDto_2, http));
    }

    @Test
    public void addHit_IP_Is_Null() {
        when(http_2.getLocalAddr()).thenReturn(null);
        when(http_2.getRequestURI()).thenReturn("/ewent/1");

        assertThrows(ErrorGettingAnIpAddress.class, () -> server.addHit(requestDto, http_2));
    }

    @Test
    public void getStats_List_Is_Not_Empty() {
        String[] uris = {"stats/1", "stats/2", "stats/3"};

        when(repository.getStats(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(),
                anyBoolean()))
                .thenReturn(viewStatsList);

        List<ViewStats> viewStats = server.getStats(
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now(),
                uris,
                true);

        assertEquals(3, viewStats.size());
    }

    @Test
    void addHit_WhenRequestDtoIsNull_ThrowsValidationException() {
        RequestDto requestDto = null;

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            server.addHit(requestDto, request);
        });

        assertEquals("Не достаточно данных для статистики", exception.getMessage());
    }

    @Test
    void addHit_WhenIpIsNull_ThrowsErrorGettingAnIpAddress() {
        when(request.getLocalAddr()).thenReturn(null);

        ErrorGettingAnIpAddress exception = assertThrows(ErrorGettingAnIpAddress.class, () -> {
            server.addHit(requestDto, request);
        });

        assertTrue(exception.getMessage().contains("Ошибка получения IP адреса"));
    }

    @Test
    void addHit_WhenValidRequestDto_ReturnsTrue() {
        requestDto.setApp("testApp");
        requestDto.setUri("/test");
        requestDto.setTimestamp(LocalDateTime.now());

        when(request.getLocalAddr()).thenReturn("127.0.0.1");
        when(request.getRequestURI()).thenReturn("/test");

        EndpointHit endpointHit = EndpointHit.builder()
                .id(1L).build();
        when(repository.save(any(EndpointHit.class))).thenReturn(endpointHit);

        boolean result = server.addHit(requestDto, request);

        assertTrue(result);
    }

    @Test
    void getStats_WhenStartAfterEnd_ThrowsValidationException() {
        LocalDateTime start = LocalDateTime.of(2023, 12, 31, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 1, 0, 0);
        String[] uris = {"/test"};
        Boolean unique = false;

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            server.getStats(start, end, uris, unique);
        });

        assertTrue(exception.getMessage().contains("start = "));
        assertTrue(exception.getMessage().contains("должно быть раньше end = "));
    }

    @Test
    void getStats_WhenValidParameters_ReturnsViewStatsList() {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 31, 0, 0);
        String[] uris = {"/test1", "/test2"};
        Boolean unique = true;

        List<ViewStats> expectedStats = List.of(ViewStats.builder()
                .app("ewm")
                .uri("/events/1")
                .hits(6L)
                .build());
        when(repository.getStats(any(), any(), any(), anyBoolean())).thenReturn(expectedStats);

        List<ViewStats> result = server.getStats(start, end, uris, unique);

        assertNotNull(result);
        assertEquals(expectedStats, result);
    }
}
