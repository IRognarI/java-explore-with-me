package ru.practicum.stats.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.formatter.TimeStampFormatter;
import ru.practicum.stats.interfaces.StatsService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class StatsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StatsService service;

    @InjectMocks
    private StatsController controller;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void saveHit_shouldCallService() throws Exception {
        EndpointHitDto dto = new EndpointHitDto();
        dto.setId(1L);
        dto.setApp("test-app");
        dto.setUri("/test");
        dto.setIp("127.0.0.1");
        dto.setTimestamp(TimeStampFormatter.format(LocalDateTime.now()));

        mockMvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(service, times(1)).saveHit(any(EndpointHitDto.class));
    }

    @Test
    void getStats_shouldReturnStatsList() throws Exception {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 2, 0, 0, 0);
        List<String> uris = List.of("/test1", "/test2");
        boolean unique = true;

        when(service.getStats(any(), any(), anyList(), anyBoolean()))
                .thenReturn(List.of(new ViewStats()));

        mockMvc.perform(get("/stats")
                        .param("start", "2023-01-01 00:00:00")
                        .param("end", "2023-01-02 00:00:00")
                        .param("uris", "/test1", "/test2")
                        .param("unique", "true"))
                .andExpect(status().isOk());

        verify(service, times(1)).getStats(start, end, uris, unique);
    }

    @Test
    void getStats_shouldThrowExceptionWhenStartAfterEnd() throws Exception {
        when(service.getStats(any(), any(), anyList(), anyBoolean()))
                .thenThrow(new IllegalArgumentException("start date must be before end date"));

        mockMvc.perform(get("/stats")
                        .param("start", "2023-01-02 00:00:00")
                        .param("end", "2023-01-01 00:00:00"))
                .andExpect(status().isBadRequest());
    }
}