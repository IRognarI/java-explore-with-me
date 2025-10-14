package ru.practicum.ewmService.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.ewmService.dateTimeFormatter.Formatter;
import ru.practicum.ewmService.event.enums.SortingMode;

import java.time.LocalDateTime;
import java.util.List;

public record EventListRequestPublic(

        String text,
        List<Long> categories,
        Boolean paid,

        @JsonFormat(pattern = Formatter.PATTERN)
        LocalDateTime rangeStart,

        @JsonFormat(pattern = Formatter.PATTERN)
        LocalDateTime rangeEnd,
        Boolean onlyAvailable,
        SortingMode sortingMode,
        int from,
        int size) {

}