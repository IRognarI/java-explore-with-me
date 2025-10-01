package ru.practicum.dto.eventDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.dto.categoryDto.CategoryDto;
import ru.practicum.dto.eventDto.location.Location;
import ru.practicum.dto.userDto.UserDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class EventDto {
    private Long id;
    private String title;
    private String annotation;
    private String description;
    private CategoryDto categoryDto;
    private Boolean paid;
    private String eventDate;
    private Integer participantLimit;
    private String state;
    private UserDto userDto;
    private String createdOn;
    private Location location;
    private Boolean requestModeration;
}