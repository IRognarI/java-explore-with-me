package ru.practicum.ewmService.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmService.category.mapper.CategoryMapper;
import ru.practicum.ewmService.dateTimeFormatter.Formatter;
import ru.practicum.ewmService.event.dto.EventFullDto;
import ru.practicum.ewmService.event.dto.EventShortDto;
import ru.practicum.ewmService.event.dto.NewEventDto;
import ru.practicum.ewmService.event.model.Event;
import ru.practicum.ewmService.user.mapper.UserMapper;

@UtilityClass
public class EventMapper {

    public Event toEvent(NewEventDto dto) {

        Event event = new Event();
        event.setAnnotation(dto.getAnnotation());
        event.setDescription(dto.getDescription());
        event.setEventDate(dto.getEventDate());
        event.setLocation(dto.getLocation());
        event.setPaid(dto.isPaid());
        event.setParticipantLimit(dto.getParticipantLimit());
        event.setRequestModeration(dto.isRequestModeration());
        event.setTitle(dto.getTitle());
        return event;
    }

    public EventShortDto toShortDto(Event event) {
        EventShortDto dto = new EventShortDto();
        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());
        dto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        dto.setEventDate(event.getEventDate().format(Formatter.FORMATTER));
        dto.setConfirmedRequests(event.getConfirmedRequests());
        dto.setPaid(event.isPaid());
        dto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        dto.setTitle(event.getTitle());
        return dto;
    }

    public EventFullDto toFullDto(Event event) {
        EventFullDto dto = new EventFullDto();
        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());
        dto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        dto.setCreatedOn(event.getCreatedOn().format(Formatter.FORMATTER));
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate().format(Formatter.FORMATTER));
        dto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        dto.setLocation(event.getLocation());
        dto.setPaid(event.isPaid());
        dto.setParticipantLimit(event.getParticipantLimit());
        dto.setConfirmedRequests(event.getConfirmedRequests());
        dto.setPublishedOn((event.getPublishedOn() != null) ?
                event.getPublishedOn().format(Formatter.FORMATTER) :
                null);
        dto.setRequestModeration(event.isRequestModeration());
        dto.setState(event.getState().name());
        dto.setTitle(event.getTitle());
        return dto;
    }
}