package ru.practicum.ewmService.participation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Объект передачи данных, представляющий результат обновления статусов заявок на участие.
 * Содержит списки подтвержденных и отклоненных заявок на участие.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationStatusUpdateResult {
    private List<ParticipationRequestDto> confirmedRequests;
    private List<ParticipationRequestDto> rejectedRequests;
}