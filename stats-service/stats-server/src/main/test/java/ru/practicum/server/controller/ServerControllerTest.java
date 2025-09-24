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
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.server.dto.requestDto.RequestDto;
import ru.practicum.server.dto.requestDto.ViewStats;
import ru.practicum.server.formatter.TimeStampFormatter;
import ru.practicum.server.interfaces.Server;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
        when(server.addHit(Mockito.eq(requestDto), any(HttpServletRequest.class))).thenReturn(true);

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
        when(server.addHit(Mockito.eq(requestDto), any(HttpServletRequest.class))).thenReturn(false);

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
}