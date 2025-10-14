package ru.practicum.ewmService.participation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewmService.participation.dto.ParticipationRequestDto;
import ru.practicum.ewmService.participation.dto.ParticipationStatusUpdateRequest;
import ru.practicum.ewmService.participation.dto.ParticipationStatusUpdateResult;
import ru.practicum.ewmService.participation.interfaces.ParticipationService;

import java.util.List;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping
public class ParticipationController {

    private final ParticipationService participationService;

    @GetMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getUserParticipationRequests(@PathVariable long userId) {

        log.info("Get participation private GET request: userId={}", userId);
        return participationService.getUserRequests(userId);
    }

    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addParticipation(@PathVariable long userId,
                                                    @RequestParam long eventId) {

        log.info("Add participation private POST request: userId={}, eventId={}", userId, eventId);
        return participationService.addRequest(userId, eventId);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelParticipation(@PathVariable long userId,
                                                       @PathVariable long requestId) {

        log.info("Cancel participation private POST request: userId={}, requestId={}", userId, requestId);
        return participationService.cancelRequest(userId, requestId);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getEventParticipationRequests(@PathVariable long userId,
                                                                       @PathVariable long eventId) {

        log.info("Get participant requests private GET request: userId={}, eventId={}", userId, eventId);
        return participationService.getEventRequests(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationStatusUpdateResult updateParticipationStatus(
            @PathVariable long userId,
            @PathVariable long eventId,
            @Valid @RequestBody ParticipationStatusUpdateRequest updateRequest) {

        log.info("Update participation status private GET request: userId={}, eventId={}, request={}",
                userId, eventId, updateRequest);
        return participationService.updateStatus(userId, eventId, updateRequest);
    }
}