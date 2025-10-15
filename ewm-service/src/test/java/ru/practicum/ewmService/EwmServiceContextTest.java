package ru.practicum.ewmService;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.statsClient.StatsClient;

@SpringBootTest
class EwmServiceContextTest {

    @MockBean
    private StatsClient statsClient;

    @Test
    void contextLoads() {
    }
}