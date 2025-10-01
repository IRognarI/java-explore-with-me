package ru.practicum.ewm.event.service;

import org.junit.jupiter.api.Assertions;
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
    private EventDtoRequest eventDtoRequest_1;
    private EventDtoRequest eventDtoRequest_2;
    private EventDtoRequest eventDtoRequest_3;
    private EventDtoRequest eventDtoRequest_4;
    private Event event;
    private Event event_2;
    private List<Event> eventListDefault = new ArrayList<>();

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

        eventListDefault.add(event);
        eventListDefault.add(event_2);
    }

    @Test
    public void addEvent_Correct() {
        Mockito.when(eventRepository.save(Mockito.any(Event.class))).thenReturn(event);
        Mockito.when(userService.getUserById(Mockito.eq(user_1.getId()))).thenReturn(user_1);
        Mockito.when(categoryService.getCategory(Mockito.eq(category_1.getId()))).thenReturn(category_1);

        Event targetEvent = eventService.addEvent(eventDtoRequest_1, user_1.getId());

        Assertions.assertNotNull(targetEvent);
    }

    @Test
    public void addEvent_shouldThrowWhenUserNotExists() {
        Assertions.assertThrows(ObjectNotFoundException.class,
                () -> eventService.addEvent(eventDtoRequest_1, 4L));
    }

    @Test
    public void addEvent_shouldThrowWhenCategoryNotExists() {
        Mockito.when(userService.getUserById(Mockito.eq(user_1.getId()))).thenReturn(user_1);

        Assertions.assertThrows(ObjectNotFoundException.class,
                () -> eventService.addEvent(eventDtoRequest_2, user_1.getId()));
    }

    @Test
    public void addEvent_shouldThrowWhenEventDateIsNotValid() {
        Mockito.when(userService.getUserById(Mockito.eq(user_1.getId()))).thenReturn(user_1);
        Mockito.when(categoryService.getCategory(Mockito.eq(category_1.getId()))).thenReturn(category_1);

        Assertions.assertThrows(DateTimeCheckException.class,
                () -> eventService.addEvent(eventDtoRequest_3, user_1.getId()));
    }

    @Test
    public void addEvent_shouldThrowWhenEventDateIsValid() {
        Mockito.when(eventRepository.save(Mockito.any(Event.class))).thenReturn(event_2);
        Mockito.when(userService.getUserById(Mockito.eq(user_1.getId()))).thenReturn(user_1);
        Mockito.when(categoryService.getCategory(Mockito.eq(category_1.getId()))).thenReturn(category_1);

        Event targetEvent = eventService.addEvent(eventDtoRequest_4, user_1.getId());

        Assertions.assertNotNull(targetEvent);
    }

    @Test
    public void getEvents_shouldReturnEventsList() {
        int from = 0;
        int size = 10;
        Mockito.when(userService.userExists(Mockito.eq(user_1.getId()))).thenReturn(true);
        Mockito.when(eventRepository.getInitiatorEvent(Mockito.eq(from), Mockito.eq(size), Mockito.eq(user_1.getId()))).thenReturn(eventListDefault);

        List<Event> eventList = eventService.getEvents(from, size, user_1.getId());

        Assertions.assertEquals(2, eventList.size());
    }

    @Test
    public void getEvents_shouldThrowWhenUserNotPresent() {
        Mockito.when(userService.userExists(Mockito.anyLong())).thenReturn(false);

        int from = 0;
        int size = 10;

        Assertions.assertThrows(ObjectNotFoundException.class,
                () -> eventService.getEvents(from, size, user_1.getId()));
    }

    @Test
    public void getTargetEvent_ShouldBeOneEvent() {
        Long eventId = event_2.getId(), userId = user_1.getId();

        Mockito.when(userService.userExists(Mockito.anyLong())).thenReturn(true);
        Mockito.when(eventRepository.getEventByIdAndInitiator_Id(Mockito.eq(eventId), Mockito.eq(userId))).thenReturn(event_2);

        Event targetEvent = eventService.getTargetEvent(userId, eventId);

        Assertions.assertEquals(event_2.getId(), targetEvent.getId());
        Assertions.assertEquals(user_1.getId(), targetEvent.getInitiator().getId());
    }

    @Test
    public void getTargetEvent_ShouldBeThrowWhenEventNotFound() {
        Long eventId = event_2.getId(), userId = user_1.getId();

        Mockito.when(userService.userExists(Mockito.anyLong())).thenReturn(true);
        Mockito.when(eventRepository.getEventByIdAndInitiator_Id(Mockito.eq(eventId), Mockito.eq(userId))).thenReturn(null);

        Assertions.assertThrows(ObjectNotFoundException.class,
                () -> eventService.getTargetEvent(userId, eventId));
    }
}
