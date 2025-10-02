package ru.practicum.ewm.interfaces.event;

import ru.practicum.dto.eventDto.eventDtoRequest.EventDtoRequest;
import ru.practicum.ewm.model.event.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    Event addEvent(EventDtoRequest eventDtoRequest, Long userId);

    List<Event> getEvents(Integer from, Integer size, Long userId);

    Event getTargetEvent(Long userId, Long eventId);

    Event updateEvent(Long userId, Long eventId, EventDtoRequest eventDtoRequest);

    List<Event> searchEventsWithParams(Long[] userIds, String[] states, Long[] categoriesIds, LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd, Integer from, Integer size);

    Event updateEventByAdmin(Long eventId, EventDtoRequest request);
}
