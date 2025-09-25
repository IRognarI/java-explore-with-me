package ru.practicum.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.server.dto.requestDto.RequestDto;
import ru.practicum.server.dto.requestDto.ViewStats;
import ru.practicum.server.interfaces.Server;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ServerController.class)
class ServerControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private Server server;

    @Autowired
    private ObjectMapper mapper;

    private RequestDto requestDto;

    @BeforeEach
    public void setup() {
        requestDto = RequestDto.builder()
                .app("ewm")
                .uri("/events/1")
                .ip("127.0.0.1")
                .timestamp(LocalDateTime.now())
                .build();

        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        module.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        mapper.registerModule(module);

    }

    @Test
    public void addHit_Correct() {
        when(server.addHit(eq(requestDto), any(HttpServletRequest.class))).thenReturn(true);

        try {

            mvc.perform(post("/hit")
                            .content(mapper.writeValueAsString(requestDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andExpect(content().string("Информация сохранена"));
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    @Test
    public void addHit_UnCorrect() {
        when(server.addHit(eq(requestDto), any(HttpServletRequest.class))).thenReturn(false);

        try {

            mvc.perform(post("/hit")
                            .content(mapper.writeValueAsString(requestDto))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isIAmATeapot())
                    .andExpect(content().string("Возникла ошибка при сохранении статистики"));
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    @Test
    void getStats_shouldReturnOk_whenStatsListIsNotEmpty() throws Exception {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 31, 23, 59, 59);
        String[] uris = {"/events/1", "/events/2"};
        Boolean unique = true;

        ViewStats viewStats1 = ViewStats.builder()
                .app("ewm")
                .uri("/events/1")
                .hits(10l)
                .build();

        ViewStats viewStats2 = ViewStats.builder()
                .app("ewm_2")
                .uri("/events/2")
                .hits(5l)
                .build();

        List<ViewStats> statsList = List.of(viewStats1, viewStats2);

        Mockito.when(server.getStats(any(), any(), any(), any())).thenReturn(statsList);

        mvc.perform(get("/stats")
                        .param("start", "2023-01-01 00:00:00")
                        .param("end", "2023-12-31 23:59:59")
                        .param("uris", uris)
                        .param("unique", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].uri").value("/events/1"))
                .andExpect(jsonPath("$[0].hits").value(10))
                .andExpect(jsonPath("$[1].uri").value("/events/2"))
                .andExpect(jsonPath("$[1].hits").value(5));
    }

    @Test
    void getStats_shouldReturnNoContent_whenStatsListIsEmpty() throws Exception {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 31, 23, 59, 59);
        String[] uris = {"/events/1"};
        Boolean unique = false;

        List<ViewStats> emptyStatsList = List.of();

        Mockito.when(server.getStats(any(), any(), any(), any())).thenReturn(emptyStatsList);

        mvc.perform(get("/stats")
                        .param("start", "2023-01-01 00:00:00")
                        .param("end", "2023-12-31 23:59:59")
                        .param("uris", uris)
                        .param("unique", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void getStats_shouldUseDefaultUniqueValue_whenUniqueNotProvided() throws Exception {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 31, 23, 59, 59);
        String[] uris = {"/events/1"};

        ViewStats viewStats = ViewStats.builder()
                .app("ewm")
                .uri("/events/1")
                .hits(10l)
                .build();
        List<ViewStats> statsList = List.of(viewStats);

        Mockito.when(server.getStats(eq(start), eq(end), eq(uris), eq(false))).thenReturn(statsList);

        mvc.perform(get("/stats")
                        .param("start", "2023-01-01 00:00:00")
                        .param("end", "2023-12-31 23:59:59")
                        .param("uris", uris)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].uri").value("/events/1"))
                .andExpect(jsonPath("$[0].hits").value(10));
    }
}