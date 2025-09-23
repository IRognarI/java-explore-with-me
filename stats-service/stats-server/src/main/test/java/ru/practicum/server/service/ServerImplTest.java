package ru.practicum.server.service;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.server.dto.requestDto.RequestDto;
import ru.practicum.server.dto.requestDto.ViewStats;
import ru.practicum.server.exception.ErrorGettingAnIpAddress;
import ru.practicum.server.exception.ValidationException;
import ru.practicum.server.model.EndpointHit;
import ru.practicum.server.repository.JpaEndpointHit;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ServerImplTest {

    @Mock
    private JpaEndpointHit repository;


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

        Mockito.when(http.getLocalAddr()).thenReturn("127.0.0.1");
        Mockito.when(http.getRequestURI()).thenReturn("/ewent/1");

        Mockito
                .when(repository.save(Mockito.any(EndpointHit.class)))
                .thenReturn(endpointHit);

        boolean result = server.addHit(requestDto, http);

        Assertions.assertTrue(result, "Метод вернул false");

        Mockito.verify(repository, Mockito.times(1)).save(Mockito.any(EndpointHit.class));
    }

    @Test
    public void addHit_RequestDto_Is_null() {
        Assertions.assertThrows(ValidationException.class, () -> server.addHit(requestDto_2, http));
    }

    @Test
    public void addHit_IP_Is_Null() {
        Mockito.when(http_2.getLocalAddr()).thenReturn(null);
        Mockito.when(http_2.getRequestURI()).thenReturn("/ewent/1");

        Assertions.assertThrows(ErrorGettingAnIpAddress.class, () -> server.addHit(requestDto, http_2));
    }

    @Test
    public void getStats_List_Is_Not_Empty() {
        String[] uris = {"stats/1", "stats/2", "stats/3"};

        Mockito
                .when(repository.getStatsWithHist(
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(LocalDateTime.class),
                        Mockito.anyList(),
                        Mockito.anyBoolean()))
                .thenReturn(viewStatsList);

        List<ViewStats> viewStats = server.getStats(
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now(),
                uris,
                true);

        Assertions.assertEquals(3, viewStats.size());
    }
}
