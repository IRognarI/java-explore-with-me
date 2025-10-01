package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.categoryDto.CategoryDto;
import ru.practicum.dto.eventDto.EventDto;
import ru.practicum.dto.eventDto.eventDtoRequest.EventDtoRequest;
import ru.practicum.dto.eventDto.location.Location;
import ru.practicum.dto.formatter.TimeStampFormatter;
import ru.practicum.dto.userDto.UserDto;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.user.User;

@UtilityClass
public class Mapper {

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category dtoToCategory(CategoryDto categoryDto) {
        return Category.builder()
                .name(categoryDto.getName())
                .build();
    }

    public static Event requestDtoToEvent(EventDtoRequest eventDtoRequest, Category category, User user) {
        return Event.builder()
                .title(eventDtoRequest.getTitle())
                .annotation(eventDtoRequest.getAnnotation())
                .description(eventDtoRequest.getDescription())
                .eventDate(eventDtoRequest.getEventDate())
                .category(category) // нужно будет добавить state - обязательно
                // нужно добавить createdOn - обязательно
                // нужно добавить publishedOn - заполняется, когда admin переведет state в PUBLISHED
                .lat(eventDtoRequest.getLocation().getLat())
                .lon(eventDtoRequest.getLocation().getLon())
                .paid(eventDtoRequest.getPaid())
                .participantLimit(eventDtoRequest.getParticipantLimit())
                .requestModeration(eventDtoRequest.getRequestModeration())
                .initiator(user)
                .build();
    }

    public static EventDto eventToDto(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .categoryDto(Mapper.toCategoryDto(event.getCategory()))
                .paid(event.getPaid())
                .eventDate(TimeStampFormatter.format(event.getEventDate()))
                .participantLimit(event.getParticipantLimit())
                .state(String.valueOf(event.getState()))
                .userDto(Mapper.toUserDto(event.getInitiator()))
                .createdOn(TimeStampFormatter.format(event.getCreatedOn()))
                .location(new Location(event.getLat(), event.getLon()))
                .requestModeration(event.getRequestModeration())
                .build();
    }
}
