package ru.practicum.ewm.event.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.repository.event.EventRepository;
import ru.practicum.ewm.service.event.EventServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceImplSearchEventWithParamsMethodTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    void searchEventsWithParams_ShouldCallRepositoryMethodWithCorrectParameters() {
        // Given
        Long[] userIds = {1L, 2L};
        String[] states = {"PENDING", "PUBLISHED"};
        Long[] categoriesIds = {10L, 20L};
        LocalDateTime rangeStart = LocalDateTime.now().minusDays(1);
        LocalDateTime rangeEnd = LocalDateTime.now().plusDays(1);
        Integer from = 0;
        Integer size = 10;

        List<Event> expectedEvents = List.of(new Event(), new Event());
        when(eventRepository.getEventsWithParams(userIds, states, categoriesIds, rangeStart, rangeEnd, from, size))
                .thenReturn(expectedEvents);

        // When
        List<Event> actualEvents = eventService.searchEventsWithParams(userIds, states, categoriesIds, rangeStart, rangeEnd, from, size);

        // Then
        assertEquals(expectedEvents.size(), actualEvents.size());
        verify(eventRepository, times(1)).getEventsWithParams(userIds, states, categoriesIds, rangeStart, rangeEnd, from, size);
    }

    @Test
    void searchEventsWithParams_ShouldHandleNullParameters() {
        // Given
        List<Event> expectedEvents = List.of();
        when(eventRepository.getEventsWithParams(null, null, null, null, null, null, null))
                .thenReturn(expectedEvents);

        // When
        List<Event> actualEvents = eventService.searchEventsWithParams(null, null, null, null, null, null, null);

        // Then
        assertEquals(expectedEvents.size(), actualEvents.size());
        verify(eventRepository, times(1)).getEventsWithParams(null, null, null, null, null, null, null);
    }
}