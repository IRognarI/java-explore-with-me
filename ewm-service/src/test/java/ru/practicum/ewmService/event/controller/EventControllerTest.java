package ru.practicum.ewmService.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewmService.category.dto.CategoryDto;
import ru.practicum.ewmService.dateTimeFormatter.Formatter;
import ru.practicum.ewmService.event.dto.EventFullDto;
import ru.practicum.ewmService.event.dto.EventShortDto;
import ru.practicum.ewmService.event.dto.NewEventDto;
import ru.practicum.ewmService.event.dto.UpdateEventRequest;
import ru.practicum.ewmService.event.enums.EventState;
import ru.practicum.ewmService.event.enums.EventStateAction;
import ru.practicum.ewmService.event.interfaces.EventService;
import ru.practicum.ewmService.event.location.Location;
import ru.practicum.ewmService.user.dto.UserShortDto;
import ru.practicum.statsClient.StatsClient;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    @MockBean
    private StatsClient statsClient;

    @Test
    void getEventsPublic() throws Exception {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Test Category");

        UserShortDto initiatorDto = new UserShortDto();
        initiatorDto.setId(1L);
        initiatorDto.setName("Initiator");

        Location location = new Location();
        location.setLat(55.7558f);
        location.setLon(37.6173f);

        EventShortDto eventDto = new EventShortDto();
        eventDto.setId(1L);
        eventDto.setTitle("Test Event");
        eventDto.setAnnotation("Test Annotation");
        eventDto.setCategory(categoryDto);
        eventDto.setConfirmedRequests(5L);
        eventDto.setEventDate(LocalDateTime.now().plusDays(1).toString());
        eventDto.setInitiator(initiatorDto);
        eventDto.setPaid(false);
        eventDto.setViews(10L);

        List<EventShortDto> events = List.of(eventDto);

        when(eventService.getEventsPublic(any())).thenReturn(events);

        mockMvc.perform(get("/events")
                        .param("text", "test")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(events)));

        verify(eventService).getEventsPublic(any());
    }

    @Test
    void getEventPublic() throws Exception {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Test Category");

        UserShortDto initiatorDto = new UserShortDto();
        initiatorDto.setId(1L);
        initiatorDto.setName("Initiator");

        Location location = new Location();
        location.setLat(55.7558f);
        location.setLon(37.6173f);

        EventFullDto eventDto = new EventFullDto();
        eventDto.setId(1L);
        eventDto.setTitle("Test Event");
        eventDto.setAnnotation("Test Annotation");
        eventDto.setCategory(categoryDto);
        eventDto.setConfirmedRequests(0L);
        eventDto.setCreatedOn(LocalDateTime.now().minusDays(1).toString());
        eventDto.setDescription("Test Description");
        eventDto.setEventDate(LocalDateTime.now().plusDays(1).toString());
        eventDto.setInitiator(initiatorDto);
        eventDto.setLocation(location);
        eventDto.setPaid(false);
        eventDto.setParticipantLimit(100);
        eventDto.setPublishedOn(LocalDateTime.now().toString());
        eventDto.setRequestModeration(true);
        eventDto.setState(EventState.PUBLISHED.name());
        eventDto.setViews(0L);

        when(eventService.getEventPublic(anyLong())).thenReturn(eventDto);

        mockMvc.perform(get("/events/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(eventDto)));

        verify(eventService).getEventPublic(1L);
    }

    @Test
    void addEvent() throws Exception {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Test Category");

        UserShortDto initiatorDto = new UserShortDto();
        initiatorDto.setId(1L);
        initiatorDto.setName("Initiator");

        Location location = new Location();
        location.setLat(55.7558f);
        location.setLon(37.6173f);

        LocalDateTime eventDateTime = LocalDateTime.now().plusHours(3).withNano(0);

        NewEventDto newEventDto = new NewEventDto();
        newEventDto.setTitle("New Event Title With Minimum Length");
        newEventDto.setAnnotation("New Annotation With Minimum Length Required For Validation");
        newEventDto.setDescription("New Description With Sufficient Length For Validation Requirements");
        newEventDto.setCategory(1L);
        newEventDto.setEventDate(eventDateTime);
        newEventDto.setLocation(location);
        newEventDto.setPaid(false);
        newEventDto.setParticipantLimit(50);
        newEventDto.setRequestModeration(true);

        EventFullDto eventDto = new EventFullDto();
        eventDto.setId(1L);
        eventDto.setTitle("New Event Title With Minimum Length");
        eventDto.setAnnotation("New Annotation With Minimum Length Required For Validation");
        eventDto.setCategory(categoryDto);
        eventDto.setConfirmedRequests(0L);
        eventDto.setCreatedOn(LocalDateTime.now().toString());
        eventDto.setDescription("New Description With Sufficient Length For Validation Requirements");
        eventDto.setEventDate(eventDateTime.format(Formatter.FORMATTER));
        eventDto.setInitiator(initiatorDto);
        eventDto.setLocation(location);
        eventDto.setPaid(false);
        eventDto.setParticipantLimit(50);
        eventDto.setRequestModeration(true);
        eventDto.setState(EventState.PENDING.name());
        eventDto.setViews(0L);

        when(eventService.addEvent(anyLong(), any(NewEventDto.class))).thenReturn(eventDto);

        mockMvc.perform(post("/users/{userId}/events", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEventDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(eventDto)));

        verify(eventService).addEvent(1L, newEventDto);
    }

    @Test
    void getEventsPrivate() throws Exception {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Test Category");

        UserShortDto initiatorDto = new UserShortDto();
        initiatorDto.setId(1L);
        initiatorDto.setName("Initiator");

        Location location = new Location();
        location.setLat(55.7558f);
        location.setLon(37.6173f);

        EventShortDto eventDto = new EventShortDto();
        eventDto.setId(1L);
        eventDto.setTitle("User Event");
        eventDto.setAnnotation("User Annotation");
        eventDto.setCategory(categoryDto);
        eventDto.setConfirmedRequests(3L);
        eventDto.setEventDate(LocalDateTime.now().plusDays(1).toString());
        eventDto.setInitiator(initiatorDto);
        eventDto.setPaid(true);
        eventDto.setViews(15L);

        List<EventShortDto> events = List.of(eventDto);

        when(eventService.getEventsPrivate(anyLong(), anyInt(), anyInt())).thenReturn(events);

        mockMvc.perform(get("/users/{userId}/events", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(events)));

        verify(eventService).getEventsPrivate(1L, 0, 10);
    }

    @Test
    void getEventPrivate() throws Exception {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Test Category");

        UserShortDto initiatorDto = new UserShortDto();
        initiatorDto.setId(1L);
        initiatorDto.setName("Initiator");

        Location location = new Location();
        location.setLat(55.7558f);
        location.setLon(37.6173f);

        EventFullDto eventDto = new EventFullDto();
        eventDto.setId(1L);
        eventDto.setTitle("Private Event");
        eventDto.setAnnotation("Private Annotation");
        eventDto.setCategory(categoryDto);
        eventDto.setConfirmedRequests(0L);
        eventDto.setCreatedOn(LocalDateTime.now().minusDays(1).toString());
        eventDto.setDescription("Private Description");
        eventDto.setEventDate(LocalDateTime.now().plusDays(1).toString());
        eventDto.setInitiator(initiatorDto);
        eventDto.setLocation(location);
        eventDto.setPaid(false);
        eventDto.setParticipantLimit(100);
        eventDto.setPublishedOn(LocalDateTime.now().toString());
        eventDto.setRequestModeration(true);
        eventDto.setState(EventState.PUBLISHED.name());
        eventDto.setViews(0L);

        when(eventService.getEventPrivate(anyLong(), anyLong())).thenReturn(eventDto);

        mockMvc.perform(get("/users/{userId}/events/{eventId}", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(eventDto)));

        verify(eventService).getEventPrivate(1L, 1L);
    }

    @Test
    void updateEventPrivate() throws Exception {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Test Category");

        UserShortDto initiatorDto = new UserShortDto();
        initiatorDto.setId(1L);
        initiatorDto.setName("Initiator");

        Location location = new Location();
        location.setLat(55.7558f);
        location.setLon(37.6173f);

        UpdateEventRequest updateRequest = new UpdateEventRequest();
        updateRequest.setAnnotation("Updated Annotation With Required Minimum Length For Validation");
        updateRequest.setDescription("Updated Description With Required Minimum Length For Validation");

        EventFullDto eventDto = new EventFullDto();
        eventDto.setId(1L);
        eventDto.setTitle("Updated Event");
        eventDto.setAnnotation("Updated Annotation With Required Minimum Length For Validation");
        eventDto.setCategory(categoryDto);
        eventDto.setConfirmedRequests(0L);
        eventDto.setCreatedOn(LocalDateTime.now().minusDays(1).toString());
        eventDto.setDescription("Updated Description With Required Minimum Length For Validation");
        eventDto.setEventDate(LocalDateTime.now().plusDays(1).format(Formatter.FORMATTER));
        eventDto.setInitiator(initiatorDto);
        eventDto.setLocation(location);
        eventDto.setPaid(false);
        eventDto.setParticipantLimit(100);
        eventDto.setPublishedOn(LocalDateTime.now().toString());
        eventDto.setRequestModeration(true);
        eventDto.setState(EventState.PENDING.name());
        eventDto.setViews(0L);

        when(eventService.updateEventPrivate(anyLong(), anyLong(), any(UpdateEventRequest.class))).thenReturn(eventDto);

        mockMvc.perform(patch("/users/{userId}/events/{eventId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(eventDto)));

        verify(eventService).updateEventPrivate(1L, 1L, updateRequest);
    }

    @Test
    void getEventsAdmin() throws Exception {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Test Category");

        UserShortDto initiatorDto = new UserShortDto();
        initiatorDto.setId(1L);
        initiatorDto.setName("Initiator");

        Location location = new Location();
        location.setLat(55.7558f);
        location.setLon(37.6173f);

        EventFullDto eventDto = new EventFullDto();
        eventDto.setId(1L);
        eventDto.setTitle("Admin Event");
        eventDto.setAnnotation("Admin Annotation");
        eventDto.setCategory(categoryDto);
        eventDto.setConfirmedRequests(0L);
        eventDto.setCreatedOn(LocalDateTime.now().minusDays(1).toString());
        eventDto.setDescription("Admin Description");
        eventDto.setEventDate(LocalDateTime.now().plusDays(1).toString());
        eventDto.setInitiator(initiatorDto);
        eventDto.setLocation(location);
        eventDto.setPaid(false);
        eventDto.setParticipantLimit(100);
        eventDto.setPublishedOn(LocalDateTime.now().toString());
        eventDto.setRequestModeration(true);
        eventDto.setState(EventState.PUBLISHED.name());
        eventDto.setViews(0L);

        List<EventFullDto> events = List.of(eventDto);

        when(eventService.getEventsAdmin(any())).thenReturn(events);

        mockMvc.perform(get("/admin/events")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(events)));

        verify(eventService).getEventsAdmin(any());
    }

    @Test
    void updateEventAdmin() throws Exception {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Test Category");

        UserShortDto initiatorDto = new UserShortDto();
        initiatorDto.setId(1L);
        initiatorDto.setName("Initiator");

        Location location = new Location();
        location.setLat(55.7558f);
        location.setLon(37.6173f);

        UpdateEventRequest updateRequest = new UpdateEventRequest();
        updateRequest.setStateAction(EventStateAction.PUBLISH_EVENT);
        updateRequest.setAnnotation("Admin Updated Annotation");

        EventFullDto eventDto = new EventFullDto();
        eventDto.setId(1L);
        eventDto.setTitle("Admin Updated Event");
        eventDto.setAnnotation("Admin Updated Annotation");
        eventDto.setCategory(categoryDto);
        eventDto.setConfirmedRequests(0L);
        eventDto.setCreatedOn(LocalDateTime.now().minusDays(1).toString());
        eventDto.setDescription("Admin Updated Description");
        eventDto.setEventDate(LocalDateTime.now().plusDays(1).toString());
        eventDto.setInitiator(initiatorDto);
        eventDto.setLocation(location);
        eventDto.setPaid(false);
        eventDto.setParticipantLimit(100);
        eventDto.setPublishedOn(LocalDateTime.now().toString());
        eventDto.setRequestModeration(true);
        eventDto.setState(EventState.PUBLISHED.name());
        eventDto.setViews(0L);

        when(eventService.updateEventAdmin(anyLong(), any(UpdateEventRequest.class))).thenReturn(eventDto);

        mockMvc.perform(patch("/admin/events/{eventId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(eventDto)));

        verify(eventService).updateEventAdmin(1L, updateRequest);
    }
}