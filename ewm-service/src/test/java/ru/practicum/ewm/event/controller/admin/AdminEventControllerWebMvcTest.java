package ru.practicum.ewm.event.controller.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.eventDto.eventDtoRequest.EventDtoRequest;
import ru.practicum.dto.eventDto.location.Location;
import ru.practicum.ewm.controller.admin.event.AdminEventController;
import ru.practicum.ewm.enums.State;
import ru.practicum.ewm.interfaces.event.EventService;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminEventController.class)
public class AdminEventControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    private EventDtoRequest eventDtoRequest_1;
    private Event event_1;
    private Category category_1;
    private User user_1;

    @BeforeEach
    public void setUp() {
        category_1 = Category.builder().id(1L).name("category#1").eventList(List.of()).build();

        user_1 = User.builder().id(1L).name("name#1").email("someEmail@mail.ru").eventList(List.of()).build();

        eventDtoRequest_1 = EventDtoRequest.builder()
                .annotation("SomeAnnotation#1")
                .category(category_1.getId().intValue())
                .description("SomeDescription#1")
                .eventDate(LocalDateTime.of(2025, 11, 12, 13, 00, 00))
                .location(new Location(55.754167, 37.62))
                .paid(true)
                .participantLimit(11)
                .requestModeration(false)
                .title("SomeTitle#1")
                .build();

        event_1 = Event.builder()
                .id(1L)
                .title(eventDtoRequest_1.getTitle())
                .annotation(eventDtoRequest_1.getAnnotation())
                .description(eventDtoRequest_1.getDescription())
                .eventDate(eventDtoRequest_1.getEventDate())
                .category(category_1)
                .state(State.PENDING)
                .createdOn(LocalDateTime.now())
                .publishedOn(null)
                .lat(eventDtoRequest_1.getLocation().getLat())
                .lon(eventDtoRequest_1.getLocation().getLon())
                .paid(eventDtoRequest_1.getPaid())
                .participantLimit(eventDtoRequest_1.getParticipantLimit())
                .requestModeration(eventDtoRequest_1.getRequestModeration())
                .initiator(user_1)
                .build();
    }

    @Test
    void searchEventsWithParams_shouldReturnEvents() throws Exception {

        Mockito.when(eventService.searchEventsWithParams(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
        )).thenReturn(List.of(event_1));

        mockMvc.perform(get("/admin/events")
                        .param("users", "1", "2")
                        .param("states", "PENDING", "PUBLISHED")
                        .param("categories", "1", "2")
                        .param("rangeStart", LocalDateTime.of(2025, 9, 02, 23, 00, 00).toString())
                        .param("rangeEnd", LocalDateTime.of(2025, 10, 02, 21, 00, 00).toString())
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1));

        Mockito.verify(eventService).searchEventsWithParams(
                eq(new Long[]{1L, 2L}),
                eq(new String[]{"PENDING", "PUBLISHED"}),
                eq(new Long[]{1L, 2L}),
                any(),
                any(),
                eq(0),
                eq(10)
        );
    }

    @Test
    void searchEventsWithParams_withoutParams_shouldReturnEvents() throws Exception {

        Mockito.when(eventService.searchEventsWithParams(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
        )).thenReturn(List.of(event_1));

        mockMvc.perform(get("/admin/events")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1));

        Mockito.verify(eventService).searchEventsWithParams(
                eq(null),
                eq(null),
                eq(null),
                eq(null),
                eq(null),
                eq(null),
                eq(null)
        );
    }
}
