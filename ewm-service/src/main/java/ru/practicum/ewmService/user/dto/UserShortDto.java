package ru.practicum.ewmService.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Объект передачи данных (DTO) для представления краткой информации о пользователе.
 * Содержит только основные поля: id и name.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserShortDto {

    private long id;
    private String name;
}