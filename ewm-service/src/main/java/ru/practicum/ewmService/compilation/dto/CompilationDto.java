package ru.practicum.ewmService.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmService.event.dto.EventShortDto;

import java.util.List;

/**
 * Data Transfer Object (DTO) для представления подборки событий.
 * Этот класс включает идентификатор подборки, заголовок, статус закрепления и список связанных событий.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {

    private long id;
    private String title;
    private boolean pinned;
    private List<EventShortDto> events;
}