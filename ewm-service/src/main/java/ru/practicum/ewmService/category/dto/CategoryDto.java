package ru.practicum.ewmService.category.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Объект передачи данных (DTO) для информации о категории.
 * Этот класс инкапсулирует данные категории, включая её уникальный идентификатор и наименование.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CategoryDto {
    private long id;
    private String name;
}