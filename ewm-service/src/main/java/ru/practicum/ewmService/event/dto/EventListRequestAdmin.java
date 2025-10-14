package ru.practicum.ewmService.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.ewmService.dateTimeFormatter.Formatter;
import ru.practicum.ewmService.event.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

public record EventListRequestAdmin(
        List<Long> users,
        List<EventState> states,
        List<Long> categories,

        @JsonFormat(pattern = Formatter.PATTERN)
        LocalDateTime rangeStart,

        @JsonFormat(pattern = Formatter.PATTERN)
        LocalDateTime rangeEnd,

        int from,
        int size) {
}