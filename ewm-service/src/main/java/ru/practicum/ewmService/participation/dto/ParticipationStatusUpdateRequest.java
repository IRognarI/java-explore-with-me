package ru.practicum.ewmService.participation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmService.participation.enums.ParticipationStatus;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationStatusUpdateRequest {
    private List<Long> ids;
    private ParticipationStatus status;
}