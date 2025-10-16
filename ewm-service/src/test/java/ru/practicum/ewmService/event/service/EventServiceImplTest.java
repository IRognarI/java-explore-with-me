package ru.practicum.ewmService.event.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewmService.category.model.Category;
import ru.practicum.ewmService.category.repository.CategoryRepository;
import ru.practicum.ewmService.event.dto.EventFullDto;
import ru.practicum.ewmService.event.dto.NewEventDto;
import ru.practicum.ewmService.event.enums.EventState;
import ru.practicum.ewmService.event.model.Event;
import ru.practicum.ewmService.event.repository.EventRepository;
import ru.practicum.ewmService.exceptions.IntegrityException;
import ru.practicum.ewmService.exceptions.NotFoundException;
import ru.practicum.ewmService.user.model.User;
import ru.practicum.ewmService.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    void addEvent_Success() {
        long userId = 1L;
        long categoryId = 1L;

        User user = new User();
        user.setId(userId);

        Category category = new Category();
        category.setId(categoryId);

        NewEventDto dto = NewEventDto.builder()
                .category(categoryId)
                .eventDate(LocalDateTime.now().plusHours(3))
                .annotation("Test annotation with minimum length required")
                .description("Test description with sufficient length for validation requirements")
                .title("Test title with minimum length")
                .build();

        Event event = new Event();
        event.setId(1L);
        event.setInitiator(user);
        event.setCategory(category);
        event.setState(EventState.PENDING);
        event.setCreatedOn(LocalDateTime.now());
        event.setEventDate(dto.getEventDate());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        EventFullDto result = eventService.addEvent(userId, dto);

        assertNotNull(result);
        assertEquals(event.getId(), result.getId());
        verify(userRepository).findById(userId);
        verify(categoryRepository).findById(categoryId);
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void addEvent_UserNotFound_ThrowsNotFoundException() {
        long userId = 1L;
        long categoryId = 1L;

        NewEventDto dto = NewEventDto.builder()
                .category(categoryId)
                .eventDate(LocalDateTime.now().plusHours(3))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> eventService.addEvent(userId, dto));
    }

    @Test
    void addEvent_CategoryNotFound_ThrowsNotFoundException() {
        long userId = 1L;
        long categoryId = 1L;

        User user = new User();
        user.setId(userId);

        NewEventDto dto = NewEventDto.builder()
                .category(categoryId)
                .eventDate(LocalDateTime.now().plusHours(3))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> eventService.addEvent(userId, dto));
    }

    @Test
    void addEvent_EventDateTooEarly_ThrowsIntegrityException() {
        long userId = 1L;
        long categoryId = 1L;

        User user = new User();
        user.setId(userId);

        Category category = new Category();
        category.setId(categoryId);

        NewEventDto dto = NewEventDto.builder()
                .category(categoryId)
                .eventDate(LocalDateTime.now().plusHours(1))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        assertThrows(IntegrityException.class, () -> eventService.addEvent(userId, dto));
    }
}