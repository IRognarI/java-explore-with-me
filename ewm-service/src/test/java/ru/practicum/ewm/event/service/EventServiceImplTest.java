package ru.practicum.ewm.event.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import ru.practicum.ewm.repository.event.JpaEventRepository;
import ru.practicum.ewm.service.category.CategoryServiceImpl;
import ru.practicum.ewm.service.event.EventServiceImpl;
import ru.practicum.ewm.service.user.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {

    @Mock
    private JpaEventRepository eventRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private CategoryServiceImpl categoryService;

    @InjectMocks
    private EventServiceImpl eventService;

    private Category category_1;
    private User user_1;
    private User user_2;
    private EventDtoRequest eventDtoRequest_1;
    private EventDtoRequest eventDtoRequest_2;
    private EventDtoRequest eventDtoRequest_3;
    private EventDtoRequest eventDtoRequest_4;
    private EventDtoRequest requestForUpdate;
    private Event event;
    private Event event_2;
    private Event event_3;
    private List<Event> eventListDefault = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        category_1 = Category.builder().id(1L).name("category#1").eventList(List.of()).build();

        user_1 = User.builder().id(1L).name("name#1").email("someEmail@mail.ru").eventList(List.of()).build();
        user_2 = User.builder().id(1L).name("Test User").email("test@example.com").build();

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

        eventDtoRequest_2 = EventDtoRequest.builder()
                .annotation("SomeAnnotation#2")
                .category(4)
                .description("SomeDescription#2")
                .eventDate(LocalDateTime.of(2025, 11, 12, 13, 00, 00))
                .location(new Location(55.754167, 37.62))
                .paid(true)
                .participantLimit(11)
                .requestModeration(false)
                .title("SomeTitle#2")
                .build();

        eventDtoRequest_3 = EventDtoRequest.builder()
                .annotation("SomeAnnotation#3")
                .category(category_1.getId().intValue())
                .description("SomeDescription#3")
                .eventDate(LocalDateTime.of(2025, 10, 1, 19, 00, 00))
                .location(new Location(55.754167, 37.62))
                .paid(true)
                .participantLimit(11)
                .requestModeration(false)
                .title("SomeTitle#2")
                .build();

        eventDtoRequest_4 = EventDtoRequest.builder()
                .annotation("SomeAnnotation#4")
                .category(category_1.getId().intValue())
                .description("SomeDescription#4")
                .eventDate(LocalDateTime.now().plusHours(2).plusMinutes(1))
                .location(new Location(55.754167, 37.62))
                .paid(true)
                .participantLimit(11)
                .requestModeration(false)
                .title("SomeTitle#2")
                .build();


        event = Event.builder()
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

        event_2 = Event.builder()
                .id(2L)
                .title(eventDtoRequest_1.getTitle())
                .annotation(eventDtoRequest_1.getAnnotation())
                .description(eventDtoRequest_1.getDescription())
                .eventDate(eventDtoRequest_4.getEventDate())
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

        event_3 = Event.builder()
                .id(3L)
                .annotation("Old annotation")
                .category(category_1)
                .description("Old description")
                .eventDate(LocalDateTime.now().plusDays(2))
                .lat(55.7558)
                .lon(37.6173)
                .paid(false)
                .participantLimit(100)
                .requestModeration(true)
                .state(State.PENDING)
                .title("Old Title")
                .initiator(user_2)
                .build();

        requestForUpdate = new EventDtoRequest();
        requestForUpdate.setAnnotation("New annotation");
        requestForUpdate.setCategory(category_1.getId().intValue());
        requestForUpdate.setDescription("New description");
        requestForUpdate.setEventDate(LocalDateTime.now().plusDays(3));
        requestForUpdate.setLocation(new Location(59.9343f, 30.3351f));
        requestForUpdate.setPaid(true);
        requestForUpdate.setParticipantLimit(200);
        requestForUpdate.setRequestModeration(false);
        requestForUpdate.setStateAction("PUBLISH_EVENT");
        requestForUpdate.setTitle("New Title");

        eventListDefault.add(event);
        eventListDefault.add(event_2);
    }

    @Test
    public void addEvent_Correct() {
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(userService.getUserById(Mockito.eq(user_1.getId()))).thenReturn(user_1);
        when(categoryService.getCategory(Mockito.eq(category_1.getId()))).thenReturn(category_1);

        Event targetEvent = eventService.addEvent(eventDtoRequest_1, user_1.getId());

        assertNotNull(targetEvent);
    }

    @Test
    public void addEvent_shouldThrowWhenUserNotExists() {
        assertThrows(ObjectNotFoundException.class,
                () -> eventService.addEvent(eventDtoRequest_1, 4L));
    }

    @Test
    public void addEvent_shouldThrowWhenCategoryNotExists() {
        when(userService.getUserById(Mockito.eq(user_1.getId()))).thenReturn(user_1);

        assertThrows(ObjectNotFoundException.class,
                () -> eventService.addEvent(eventDtoRequest_2, user_1.getId()));
    }

    @Test
    public void addEvent_shouldThrowWhenEventDateIsNotValid() {
        when(userService.getUserById(Mockito.eq(user_1.getId()))).thenReturn(user_1);
        when(categoryService.getCategory(Mockito.eq(category_1.getId()))).thenReturn(category_1);

        assertThrows(DateTimeCheckException.class,
                () -> eventService.addEvent(eventDtoRequest_3, user_1.getId()));
    }

    @Test
    public void addEvent_shouldThrowWhenEventDateIsValid() {
        when(eventRepository.save(any(Event.class))).thenReturn(event_2);
        when(userService.getUserById(Mockito.eq(user_1.getId()))).thenReturn(user_1);
        when(categoryService.getCategory(Mockito.eq(category_1.getId()))).thenReturn(category_1);

        Event targetEvent = eventService.addEvent(eventDtoRequest_4, user_1.getId());

        assertNotNull(targetEvent);
    }

    @Test
    public void getEvents_shouldReturnEventsList() {
        int from = 0;
        int size = 10;
        when(userService.userExists(Mockito.eq(user_1.getId()))).thenReturn(true);
        when(eventRepository.getInitiatorEvent(Mockito.eq(from), Mockito.eq(size), Mockito.eq(user_1.getId()))).thenReturn(eventListDefault);

        List<Event> eventList = eventService.getEvents(from, size, user_1.getId());

        assertEquals(2, eventList.size());
    }

    @Test
    public void getEvents_shouldThrowWhenUserNotPresent() {
        when(userService.userExists(Mockito.anyLong())).thenReturn(false);

        int from = 0;
        int size = 10;

        assertThrows(ObjectNotFoundException.class,
                () -> eventService.getEvents(from, size, user_1.getId()));
    }

    @Test
    public void getTargetEvent_ShouldBeOneEvent() {
        Long eventId = event_2.getId(), userId = user_1.getId();

        when(userService.userExists(Mockito.anyLong())).thenReturn(true);
        when(eventRepository.getEventByIdAndInitiator_Id(Mockito.eq(eventId), Mockito.eq(userId))).thenReturn(event_2);

        Event targetEvent = eventService.getTargetEvent(userId, eventId);

        assertEquals(event_2.getId(), targetEvent.getId());
        assertEquals(user_1.getId(), targetEvent.getInitiator().getId());
    }

    @Test
    public void getTargetEvent_ShouldBeThrowWhenEventNotFound() {
        Long eventId = event_2.getId(), userId = user_1.getId();

        when(userService.userExists(Mockito.anyLong())).thenReturn(true);
        when(eventRepository.getEventByIdAndInitiator_Id(Mockito.eq(eventId), Mockito.eq(userId))).thenReturn(null);

        assertThrows(ObjectNotFoundException.class,
                () -> eventService.getTargetEvent(userId, eventId));
    }

    @Test
    void updateEventByAdmin_shouldThrowValidationException_whenEventIdIsNull() {
        assertThrows(ValidationException.class, () -> eventService.updateEventByAdmin(null, requestForUpdate));
    }

    @Test
    void updateEventByAdmin_shouldThrowObjectNotFoundException_whenEventNotFound() {
        when(eventRepository.getEventById(1L)).thenReturn(null);

        assertThrows(ObjectNotFoundException.class, () -> eventService.updateEventByAdmin(1L, requestForUpdate));
    }

    @Test
    void updateEventByAdmin_shouldThrowValidationException_whenRequestIsNull() {
        when(eventRepository.getEventById(1L)).thenReturn(event_3);

        assertThrows(ValidationException.class, () -> eventService.updateEventByAdmin(1L, null));
    }

    @Test
    void updateEventByAdmin_shouldThrowDateTimeCheckException_whenEventDateIsTooSoon() {
        when(eventRepository.getEventById(1L)).thenReturn(event_3);
        requestForUpdate.setEventDate(LocalDateTime.now().plusMinutes(30));

        assertThrows(DateTimeCheckException.class, () -> eventService.updateEventByAdmin(1L, requestForUpdate));
    }

    @Test
    void updateEventByAdmin_shouldThrowStateValidationException_whenEventIsPublished() {
        event_3.setState(State.PUBLISHED);
        when(eventRepository.getEventById(1L)).thenReturn(event_3);

        assertThrows(StateValidationException.class, () -> eventService.updateEventByAdmin(1L, requestForUpdate));
    }

    @Test
    void updateEventByAdmin_shouldThrowStateValidationException_whenInvalidStateAction() {
        when(eventRepository.getEventById(1L)).thenReturn(event_3);
        requestForUpdate.setStateAction("INVALID_ACTION");

        assertThrows(StateValidationException.class, () -> eventService.updateEventByAdmin(1L, requestForUpdate));
    }

    @Test
    void updateEventByAdmin_shouldPublishEvent_whenStateActionIsPUBLISH_EVENT() {
        when(eventRepository.getEventById(1L)).thenReturn(event_3);
        when(categoryService.getCategory(1L)).thenReturn(category_1);
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event result = eventService.updateEventByAdmin(1L, requestForUpdate);

        assertEquals(State.PUBLISHED, result.getState());
        assertNotNull(result.getPublishedOn());
        assertEquals(requestForUpdate.getAnnotation(), result.getAnnotation());
        assertEquals(requestForUpdate.getDescription(), result.getDescription());
        assertEquals(requestForUpdate.getEventDate(), result.getEventDate());
        assertEquals(requestForUpdate.getLocation().getLat(), result.getLat());
        assertEquals(requestForUpdate.getLocation().getLon(), result.getLon());
        assertEquals(requestForUpdate.getPaid(), result.getPaid());
        assertEquals(requestForUpdate.getParticipantLimit(), result.getParticipantLimit());
        assertEquals(requestForUpdate.getRequestModeration(), result.getRequestModeration());
        assertEquals(requestForUpdate.getTitle(), result.getTitle());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void updateEventByAdmin_shouldRejectEvent_whenStateActionIsREJECT_EVENT() {
        when(eventRepository.getEventById(1L)).thenReturn(event_3);
        requestForUpdate.setStateAction("REJECT_EVENT");

        when(categoryService.getCategory(1L)).thenReturn(category_1);
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event result = eventService.updateEventByAdmin(1L, requestForUpdate);

        assertEquals(State.CANCELED, result.getState());
        assertNull(result.getPublishedOn());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void updateEventByAdmin_shouldUseExistingValuesWhenRequestFieldsAreNull() {
        when(eventRepository.getEventById(1L)).thenReturn(event_3);
        requestForUpdate.setAnnotation(null);
        requestForUpdate.setDescription(null);
        requestForUpdate.setEventDate(null);
        requestForUpdate.setLocation(null);
        requestForUpdate.setPaid(null);
        requestForUpdate.setParticipantLimit(null);
        requestForUpdate.setRequestModeration(null);
        requestForUpdate.setTitle(null);

        when(categoryService.getCategory(1L)).thenReturn(category_1);
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event result = eventService.updateEventByAdmin(1L, requestForUpdate);

        assertEquals(event_3.getAnnotation(), result.getAnnotation());
        assertEquals(event_3.getDescription(), result.getDescription());
        assertEquals(event_3.getEventDate(), result.getEventDate());
        assertEquals(event_3.getLat(), result.getLat());
        assertEquals(event_3.getLon(), result.getLon());
        assertEquals(event_3.getPaid(), result.getPaid());
        assertEquals(event_3.getParticipantLimit(), result.getParticipantLimit());
        assertEquals(event_3.getRequestModeration(), result.getRequestModeration());
        assertEquals(event_3.getTitle(), result.getTitle());
        verify(eventRepository, times(1)).save(any(Event.class));
    }
}
