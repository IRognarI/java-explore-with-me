package ru.practicum.ewm.interfaces.event;

import ru.practicum.dto.eventDto.eventDtoRequest.EventDtoRequest;
import ru.practicum.ewm.model.event.Event;

import java.util.List;

public interface EventService {
    Event addEvent(EventDtoRequest eventDtoRequest, Long userId);

    List<Event> getEvents(Integer from, Integer size, Long userId);

    Event getTargetEvent(Long userId, Long eventId);
}
