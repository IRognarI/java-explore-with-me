package ru.practicum.ewm.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.practicum.ewm.controller.internal.event.EventController;
import ru.practicum.ewm.enums.State;
import ru.practicum.ewm.interfaces.event.EventService;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
public class EventControllerWebMvcTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EventService eventService;

    private EventDtoRequest eventDtoRequest_1;
    private Event event_1;
    private Category category_1;
    private User user_1;
    private List<Event> eventListDefault = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        eventDtoRequest_1 = EventDtoRequest.builder()
                .annotation("SomeAnnotation#1")
                .category(3)
                .description("SomeDescription#1")
                .eventDate(LocalDateTime.of(2025, 11, 12, 13, 00, 00))
                .location(new Location(55.754167, 37.62))
                .paid(true)
                .participantLimit(11)
                .requestModeration(false)
                .title("SomeTitle#1")
                .build();

        category_1 = Category.builder().id(1L).name("category#1").eventList(List.of()).build();

        user_1 = User.builder().id(1L).name("name#1").email("someEmail@mail.ru").eventList(List.of()).build();

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

        eventListDefault.add(event_1);
    }

    @Test
    public void addEvent_Correct() throws Exception {
        Mockito.when(eventService.addEvent(Mockito.any(EventDtoRequest.class), Mockito.eq(1L))).thenReturn(event_1);

        mvc.perform(post("/users/1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDtoRequest_1)))
                .andExpect(status().isCreated());

        Mockito.verify(eventService, Mockito.times(1)).addEvent(Mockito.any(EventDtoRequest.class), Mockito.eq(1L));
    }

    @Test
    public void getEvents_ShouldStatusIsOk() throws Exception {
        int from = 0;
        int size = 10;
        Mockito.when(eventService.getEvents(Mockito.eq(from), Mockito.eq(size), Mockito.eq(user_1.getId())))
                .thenReturn(eventListDefault);

        mvc.perform(get("/users/1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk());

        Mockito.verify(eventService, Mockito.times(1)).getEvents(Mockito.eq(from), Mockito.eq(size), Mockito.eq(user_1.getId()));
    }

    @Test
    public void getTargetEvent_shouldBeStatusIsOk() throws Exception {
        long userId = user_1.getId();
        long eventId = event_1.getId();
        Mockito.when(eventService.getTargetEvent(Mockito.eq(userId), Mockito.eq(eventId))).thenReturn(event_1);

        mvc.perform(get("/users/1/events/1"))
                .andExpect(status().isOk());
    }
}
