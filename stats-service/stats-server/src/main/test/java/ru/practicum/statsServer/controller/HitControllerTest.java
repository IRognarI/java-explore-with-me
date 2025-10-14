package ru.practicum.statsServer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.statsDto.NewHitDto;
import ru.practicum.statsDto.StatsItemDto;
import ru.practicum.statsServer.interfaces.HitService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HitController.class)
class HitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HitService hitService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addHit_shouldReturnCreated() throws Exception {
        NewHitDto dto = new NewHitDto();
        dto.setApp("testApp");
        dto.setUri("/test");
        dto.setIp("192.168.1.1");
        dto.setTimestamp(LocalDateTime.of(2023, 1, 1, 12, 0, 0));

        mockMvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(hitService).addHit(any(NewHitDto.class));
    }

    @Test
    void addHit_withInvalidData_shouldReturnBadRequest() throws Exception {
        NewHitDto dto = new NewHitDto();
        dto.setApp("");
        dto.setUri("/test");
        dto.setIp("192.168.1.1");
        dto.setTimestamp(LocalDateTime.of(2023, 1, 1, 12, 0, 0));

        mockMvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getStats_shouldReturnStatsList() throws Exception {
        StatsItemDto statsItem = new StatsItemDto();
        statsItem.setApp("testApp");
        statsItem.setUri("/test");
        statsItem.setHits(5L);

        when(hitService.getStats(
                eq(LocalDateTime.of(2023, 1, 1, 0, 0, 0)),
                eq(LocalDateTime.of(2023, 12, 31, 23, 59, 59)),
                anyList(),
                eq(false)))
                .thenReturn(List.of(statsItem));

        mockMvc.perform(get("/stats")
                        .param("start", "2023-01-01 00:00:00")
                        .param("end", "2023-12-31 23:59:59")
                        .param("uris", "/test")
                        .param("unique", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].app").value("testApp"))
                .andExpect(jsonPath("$[0].uri").value("/test"))
                .andExpect(jsonPath("$[0].hits").value(5));
    }

    @Test
    void getStats_withInvalidDateRange_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/stats")
                        .param("start", "2023-12-31 00:00:00")
                        .param("end", "2023-01-01 00:00:00"))
                .andExpect(status().isBadRequest());
    }
}