package ru.practicum.ewm.event.service;

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
import ru.practicum.ewm.exception.StateValidationException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.repository.event.EventRepository;
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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceImplMethodUpdateEventByAdminTest {

    @Mock
    private JpaEventRepository jpaEventRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private CategoryServiceImpl categoryService;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    void updateEventByAdmin_whenRequestIsNull_thenThrowValidationException() {
        Long eventId = 1L;
        EventDtoRequest request = null;

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            eventService.updateEventByAdmin(eventId, request);
        });

        assertEquals("Не достаточно данных для обновления мероприятия", exception.getMessage());
        verify(jpaEventRepository, never()).getEventById(anyLong());
    }

    @Test
    void updateEventByAdmin_whenEventNotFound_thenThrowObjectNotFoundException() {
        Long eventId = 1L;
        EventDtoRequest request = new EventDtoRequest();

        when(jpaEventRepository.getEventById(eventId)).thenReturn(null);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () -> {
            eventService.updateEventByAdmin(eventId, request);
        });

        assertEquals("Мероприятие с ID " + eventId + " не найдено", exception.getMessage());
        verify(jpaEventRepository, times(1)).getEventById(eventId);
    }

    @Test
    void updateEventByAdmin_whenEventIsPublished_thenThrowStateValidationException() {
        Long eventId = 1L;
        EventDtoRequest request = new EventDtoRequest();
        Event event = Event.builder()
                .id(eventId)
                .state(State.PUBLISHED)
                .build();

        when(jpaEventRepository.getEventById(eventId)).thenReturn(event);

        StateValidationException exception = assertThrows(StateValidationException.class, () -> {
            eventService.updateEventByAdmin(eventId, request);
        });

        assertEquals("Нельзя редактировать мероприятие в статусе PUBLISHED", exception.getMessage());
        verify(jpaEventRepository, times(1)).getEventById(eventId);
    }

    @Test
    void updateEventByAdmin_whenEventDateIsTooEarly_thenThrowDateTimeCheckException() {
        Long eventId = 1L;
        EventDtoRequest request = new EventDtoRequest();
        request.setEventDate(LocalDateTime.now().plusMinutes(30)); // Earlier than 1 hour from now

        Event event = Event.builder()
                .id(eventId)
                .state(State.PENDING)
                .eventDate(LocalDateTime.now().plusDays(1))
                .build();

        when(jpaEventRepository.getEventById(eventId)).thenReturn(event);

        DateTimeCheckException exception = assertThrows(DateTimeCheckException.class, () -> {
            eventService.updateEventByAdmin(eventId, request);
        });

        assertTrue(exception.getMessage().contains("Время начала мероприятия должно быть не ранее чем за час от даты публикации"));
        verify(jpaEventRepository, times(1)).getEventById(eventId);
    }

    @Test
    void updateEventByAdmin_whenValidData_thenUpdateEvent() {
        Long eventId = 1L;
        Long categoryId = 2L;

        // Create request with updated data
        EventDtoRequest request = new EventDtoRequest();
        request.setAnnotation("Updated annotation");
        request.setCategory(Math.toIntExact(categoryId));
        request.setDescription("Updated description");
        request.setEventDate(LocalDateTime.now().plusHours(2));
        request.setLocation(new Location(55.7558, 37.6173));
        request.setPaid(true);
        request.setParticipantLimit(50);
        request.setRequestModeration(false);
        request.setTitle("Updated title");

        // Create existing event
        User user = User.builder().id(1L).name("User").build();
        Category oldCategory = Category.builder().id(1L).name("Old Category").build();
        Event existingEvent = Event.builder()
                .id(eventId)
                .annotation("Old annotation")
                .category(oldCategory)
                .description("Old description")
                .eventDate(LocalDateTime.now().plusDays(1))
                .lat(59.9343)
                .lon(30.3351)
                .paid(false)
                .participantLimit(100)
                .requestModeration(true)
                .state(State.PENDING)
                .title("Old title")
                .initiator(user)
                .build();

        Category newCategory = Category.builder().id(categoryId).name("New Category").build();
        Event updatedEvent = existingEvent.toBuilder()
                .annotation("Updated annotation")
                .category(newCategory)
                .description("Updated description")
                .eventDate(request.getEventDate())
                .lat(55.7558)
                .lon(37.6173)
                .paid(true)
                .participantLimit(50)
                .requestModeration(false)
                .title("Updated title")
                .build();

        when(jpaEventRepository.getEventById(eventId)).thenReturn(existingEvent);
        when(categoryService.getCategory(categoryId)).thenReturn(newCategory);
        when(jpaEventRepository.save(any(Event.class))).thenReturn(updatedEvent);

        Event result = eventService.updateEventByAdmin(eventId, request);

        assertNotNull(result);
        assertEquals("Updated annotation", result.getAnnotation());
        assertEquals(newCategory, result.getCategory());
        assertEquals("Updated description", result.getDescription());
        assertEquals(request.getEventDate(), result.getEventDate());
        assertEquals(55.7558, result.getLat());
        assertEquals(37.6173, result.getLon());
        assertTrue(result.getPaid());
        assertEquals(50, result.getParticipantLimit());
        assertFalse(result.getRequestModeration());
        assertEquals("Updated title", result.getTitle());
        assertEquals(State.PENDING, result.getState());

        verify(jpaEventRepository, times(1)).getEventById(eventId);
        verify(categoryService, times(1)).getCategory(categoryId);
        verify(jpaEventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void updateEventByAdmin_whenSomeFieldsAreNull_thenUseExistingValues() {
        Long eventId = 1L;

        EventDtoRequest request = new EventDtoRequest();
        // Only set some fields, others should remain unchanged
        request.setAnnotation("Updated annotation");
        // category is null
        // description is null
        // eventDate is null
        // location is null
        // paid is null
        // participantLimit is null
        // requestModeration is null
        // title is null

        User user = User.builder().id(1L).name("User").build();
        Category category = Category.builder().id(1L).name("Category").build();
        Event existingEvent = Event.builder()
                .id(eventId)
                .annotation("Old annotation")
                .category(category)
                .description("Old description")
                .eventDate(LocalDateTime.now().plusDays(1))
                .lat(59.9343)
                .lon(30.3351)
                .paid(false)
                .participantLimit(100)
                .requestModeration(true)
                .state(State.PENDING)
                .title("Old title")
                .initiator(user)
                .build();

        when(jpaEventRepository.getEventById(eventId)).thenReturn(existingEvent);
        when(jpaEventRepository.save(any(Event.class))).thenReturn(existingEvent);

        Event result = eventService.updateEventByAdmin(eventId, request);

        assertNotNull(result);
        assertEquals("Updated annotation", result.getAnnotation());
        assertEquals(category, result.getCategory());
        assertEquals("Old description", result.getDescription());
        assertEquals(existingEvent.getEventDate(), result.getEventDate());
        assertEquals(59.9343, result.getLat());
        assertEquals(30.3351, result.getLon());
        assertFalse(result.getPaid());
        assertEquals(100, result.getParticipantLimit());
        assertTrue(result.getRequestModeration());
        assertEquals("Old title", result.getTitle());

        verify(jpaEventRepository, times(1)).getEventById(eventId);
        verify(jpaEventRepository, times(1)).save(any(Event.class));
    }
}