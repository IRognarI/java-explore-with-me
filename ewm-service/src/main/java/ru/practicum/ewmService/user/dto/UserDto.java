package ru.practicum.ewmService.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Объект передачи данных (DTO) для информации о пользователе.
 * Этот класс используется для передачи данных пользователя между различными слоями приложения.
 * Включает идентификатор пользователя, имя и адрес электронной почты.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private long id;
    private String name;
    private String email;
}