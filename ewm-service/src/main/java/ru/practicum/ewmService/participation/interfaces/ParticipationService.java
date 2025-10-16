package ru.practicum.ewmService.participation.interfaces;

import ru.practicum.ewmService.participation.dto.ParticipationRequestDto;
import ru.practicum.ewmService.participation.dto.ParticipationStatusUpdateRequest;
import ru.practicum.ewmService.participation.dto.ParticipationStatusUpdateResult;

import java.util.List;

public interface ParticipationService {

    List<ParticipationRequestDto> getUserRequests(long userId);

    ParticipationRequestDto addRequest(long userId, long eventId);

    ParticipationRequestDto cancelRequest(long userId, long requestId);

    List<ParticipationRequestDto> getEventRequests(long userId, long eventId);

    ParticipationStatusUpdateResult updateStatus(long userId, long eventId,
                                                 ParticipationStatusUpdateRequest updateRequest);
}