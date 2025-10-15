package ru.practicum.ewmService.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * DTO для обновления подборки.
 * Содержит поля для заголовка, статуса закрепления и списка идентификаторов событий.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompilationRequest {

    @Length(min = 1, max = 50)
    private String title;
    private Boolean pinned;
    List<Long> events;
}