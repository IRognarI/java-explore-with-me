package ru.practicum.server.service;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.dto.requestDto.RequestDto;
import ru.practicum.dto.responseDto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
public class ServerImplWithDBTest {

    @Autowired
    private ServerImpl server;

    @MockBean
    private HttpServletRequest httpServletRequest;

    private RequestDto requestDto;

    @BeforeEach
    public void setup() {
        Mockito.when(httpServletRequest.getRequestURI()).thenReturn("/events/1");
        Mockito.when(httpServletRequest.getLocalAddr()).thenReturn("1.127.1.1");

        requestDto = RequestDto.builder()
                .app("emw-serv")
                .uri("/events/1")
                .ip(null)
                .timestamp(LocalDateTime.now().minusDays(4))
                .build();
    }

    @Test
    public void addHit_Correct() {
        boolean result = server.addHit(requestDto, httpServletRequest);

        Assertions.assertTrue(result);
    }

    @Test
    public void getStats_Without_Uris() {
        server.addHit(requestDto, httpServletRequest);

        List<ViewStats> viewStats = server.getStats(
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now().plusDays(3),
                null,
                false
        );

        Optional<ViewStats> result = viewStats.stream().findFirst();

        Assertions.assertFalse(result.isEmpty(), "Объект ViewStats корректно вернулся из базы");
        Assertions.assertEquals(requestDto.getApp(), result.get().getApp());
    }
}
