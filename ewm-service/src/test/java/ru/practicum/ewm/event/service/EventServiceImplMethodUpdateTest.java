package ru.practicum.ewm.event.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.dto.eventDto.eventDtoRequest.EventDtoRequest;
import ru.practicum.dto.eventDto.location.Location;
import ru.practicum.ewm.enums.State;
import ru.practicum.ewm.exception.DateTimeCheckException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.repository.event.JpaEventRepository;
import ru.practicum.ewm.service.category.CategoryServiceImpl;
import ru.practicum.ewm.service.event.EventServiceImpl;
import ru.practicum.ewm.service.user.UserServiceImpl;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceImplMethodUpdateTest {

    @Mock
    private JpaEventRepository eventRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private CategoryServiceImpl categoryService;

    @InjectMocks
    private EventServiceImpl eventService;

    private User user;
    private Category category;
    private Event event;
    private EventDtoRequest eventDtoRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .build();

        category = Category.builder()
                .id(1L)
                .name("Test Category")
                .build();

        event = Event.builder()
                .id(1L)
                .title("Test Event")
                .annotation("Test Annotation")
                .description("Test Description")
                .eventDate(LocalDateTime.now().plusDays(1))
                .lat(55.7558)
                .lon(37.6173)
                .paid(false)
                .participantLimit(100)
                .requestModeration(true)
                .initiator(user)
                .category(category)
                .state(State.PENDING)
                .build();

        eventDtoRequest = EventDtoRequest.builder()
                .title("Updated Event")
                .annotation("Updated Annotation")
                .description("Updated Description")
                .eventDate(LocalDateTime.now().plusDays(2))
                .location(new Location(59.9343, 30.3351))
                .paid(true)
                .participantLimit(200)
                .requestModeration(false)
                .category(1)
                .stateAction("PUBLISH_EVENT")
                .build();
    }

    @Test
    void updateEvent_shouldThrowValidationException_whenUserIdIsNull() {
        assertThrows(ValidationException.class, () -> eventService.updateEvent(null, 1L, eventDtoRequest));
    }

    @Test
    void updateEvent_shouldThrowValidationException_whenEventIdIsNull() {
        assertThrows(ValidationException.class, () -> eventService.updateEvent(1L, null, eventDtoRequest));
    }

    @Test
    void updateEvent_shouldThrowValidationException_whenEventDtoRequestIsNull() {
        assertThrows(ValidationException.class, () -> eventService.updateEvent(1L, 1L, null));
    }

    @Test
    void updateEvent_shouldThrowObjectNotFoundException_whenEventNotFound() {
        when(eventRepository.findEventForUpdate(1L, 1L)).thenReturn(null);

        assertThrows(ObjectNotFoundException.class, () -> eventService.updateEvent(1L, 1L, eventDtoRequest));
    }

    @Test
    void updateEvent_shouldThrowDateTimeCheckException_whenEventDateIsTooEarly() {
        eventDtoRequest.setEventDate(LocalDateTime.now().plusHours(1));
        when(eventRepository.findEventForUpdate(1L, 1L)).thenReturn(event);

        assertThrows(DateTimeCheckException.class, () -> eventService.updateEvent(1L, 1L, eventDtoRequest));
    }

    @Test
    void updateEvent_shouldUpdateEventSuccessfully() {
        when(eventRepository.findEventForUpdate(1L, 1L)).thenReturn(event);
        when(categoryService.getCategory(1L)).thenReturn(category);
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event updatedEvent = eventService.updateEvent(1L, 1L, eventDtoRequest);

        assertNotNull(updatedEvent);
        assertEquals("Updated Annotation", updatedEvent.getAnnotation());
        assertEquals("Updated Description", updatedEvent.getDescription());
        assertEquals(eventDtoRequest.getEventDate(), updatedEvent.getEventDate());
        assertEquals(59.9343, updatedEvent.getLat());
        assertEquals(30.3351, updatedEvent.getLon());
        assertTrue(updatedEvent.getPaid());
        assertEquals(200, updatedEvent.getParticipantLimit());
        assertFalse(updatedEvent.getRequestModeration());
        assertEquals(State.PENDING, updatedEvent.getState());
        assertEquals("Updated Event", updatedEvent.getTitle());

        verify(eventRepository, times(1)).findEventForUpdate(1L, 1L);
        verify(categoryService, times(1)).getCategory(1L);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void updateEvent_shouldUseExistingValuesWhenNotProvidedInRequest() {
        EventDtoRequest partialRequest = EventDtoRequest.builder()
                .category(1)
                .build();

        when(eventRepository.findEventForUpdate(1L, 1L)).thenReturn(event);
        when(categoryService.getCategory(1L)).thenReturn(category);
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event updatedEvent = eventService.updateEvent(1L, 1L, partialRequest);

        assertNotNull(updatedEvent);
        assertEquals(event.getAnnotation(), updatedEvent.getAnnotation());
        assertEquals(event.getDescription(), updatedEvent.getDescription());
        assertEquals(event.getEventDate(), updatedEvent.getEventDate());
        assertEquals(event.getLat(), updatedEvent.getLat());
        assertEquals(event.getLon(), updatedEvent.getLon());
        assertEquals(event.getPaid(), updatedEvent.getPaid());
        assertEquals(event.getParticipantLimit(), updatedEvent.getParticipantLimit());
        assertEquals(event.getRequestModeration(), updatedEvent.getRequestModeration());
        assertEquals(State.CANCELED, updatedEvent.getState());
        assertEquals(event.getTitle(), updatedEvent.getTitle());

        verify(eventRepository, times(1)).findEventForUpdate(1L, 1L);
        verify(categoryService, times(1)).getCategory(1L);
        verify(eventRepository, times(1)).save(any(Event.class));
    }
}