package ru.practicum.ewmService.event.interfaces;

import ru.practicum.ewmService.event.dto.EventFullDto;
import ru.practicum.ewmService.event.dto.EventListRequestAdmin;
import ru.practicum.ewmService.event.dto.EventListRequestPublic;
import ru.practicum.ewmService.event.dto.EventShortDto;
import ru.practicum.ewmService.event.dto.NewEventDto;
import ru.practicum.ewmService.event.dto.UpdateEventRequest;

import java.util.List;

public interface EventService {

    EventFullDto addEvent(long userId, NewEventDto dto);

    EventFullDto updateEventPrivate(long userId, long eventId, UpdateEventRequest updateRequest);

    List<EventShortDto> getEventsPrivate(long userId, int from, int size);

    EventFullDto getEventPrivate(long userId, long eventId);

    List<EventFullDto> getEventsAdmin(EventListRequestAdmin request);

    EventFullDto updateEventAdmin(long eventId, UpdateEventRequest updateRequest);

    List<EventShortDto> getEventsPublic(EventListRequestPublic request);

    EventFullDto getEventPublic(long id);
}