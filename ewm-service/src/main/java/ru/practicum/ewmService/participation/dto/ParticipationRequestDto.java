package ru.practicum.ewmService.participation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Объект передачи данных (DTO) для запросов на участие.
 * Этот класс представляет структуру данных для передачи информации о запросе на участие,
 * включая идентификатор запроса, идентификатор связанного события, идентификатор заявителя,
 * статус запроса и временную метку создания.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequestDto {
    private long id;
    private long event;
    private long requester;
    private String status;
    private String created;
}