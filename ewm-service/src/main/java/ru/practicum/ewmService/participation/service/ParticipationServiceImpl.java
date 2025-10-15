package ru.practicum.ewmService.participation.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewmService.event.enums.EventState;
import ru.practicum.ewmService.event.model.Event;
import ru.practicum.ewmService.event.repository.EventRepository;
import ru.practicum.ewmService.exceptions.IntegrityException;
import ru.practicum.ewmService.exceptions.NotFoundException;
import ru.practicum.ewmService.participation.dto.ParticipationRequestDto;
import ru.practicum.ewmService.participation.dto.ParticipationStatusUpdateRequest;
import ru.practicum.ewmService.participation.dto.ParticipationStatusUpdateResult;
import ru.practicum.ewmService.participation.enums.ParticipationStatus;
import ru.practicum.ewmService.participation.interfaces.ParticipationService;
import ru.practicum.ewmService.participation.mapper.ParticipationMapper;
import ru.practicum.ewmService.participation.model.Participation;
import ru.practicum.ewmService.participation.repository.ParticipationRepository;
import ru.practicum.ewmService.user.model.User;
import ru.practicum.ewmService.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Реализация интерфейса {@link ParticipationService}.
 * Предоставляет методы для управления запросами на участие в событиях.
 * Включает создание, отмену и обновление статуса запросов на участие,
 * а также получение запросов для пользователей или инициаторов событий.
 */
@Slf4j
@Service
@AllArgsConstructor
public class ParticipationServiceImpl implements ParticipationService {

    private final UserRepository userRepository;
    private final ParticipationRepository participationRepository;
    private final EventRepository eventRepository;

    private User checkUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("User with id=%d not found".formatted(userId)));
    }

    private Event checkEvent(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new NotFoundException("Event with id=%d not found".formatted(eventId)));
    }

    private void checkUserOwnsEvent(User user, Event event) {
        if (!Objects.equals(user.getId(), event.getInitiator().getId())) {
            throw new IntegrityException("Event with id=%d does not belong to user with id=%d"
                    .formatted(event.getId(), user.getId()));
        }
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(long userId) {

        User user = checkUser(userId);
        List<Participation> requests = participationRepository.findAllByRequester(user);
        log.info("Get participation request returns {} records", requests.size());
        return ParticipationMapper.toDtos(requests);
    }

    @Override
    public ParticipationRequestDto addRequest(long userId, long eventId) {

        User user = checkUser(userId);
        Event event = checkEvent(eventId);


        if (participationRepository.existsByRequesterAndEvent(user, event)) {
            throw new IntegrityException("Request from user id=%d to event id=%d already exists"
                    .formatted(userId, eventId));
        }

        if (event.getInitiator().getId() == userId) {
            throw new IntegrityException("User with id=%d has already owns event id=%d"
                    .formatted(userId, eventId));
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new IntegrityException("Event with id=%d has no state to publish".formatted(eventId));
        }

        if (event.isLimitExpired()) {
            throw new IntegrityException("Event with id=%d has no available to participate".formatted(eventId));
        }


        var participation = new Participation();
        participation.setRequester(user);
        participation.setEvent(event);
        participation.setCreated(LocalDateTime.now());
        if (event.isRequestModeration() && event.getParticipantLimit() > 0) {
            participation.setStatus(ParticipationStatus.PENDING);
        } else {
            participation.setStatus(ParticipationStatus.CONFIRMED);
            event.increaseConfirmedRequests();
            eventRepository.save(event);
        }
        participation = participationRepository.save(participation);
        return ParticipationMapper.toDto(participation);
    }

    @Override
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {


        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=%d not found".formatted(userId));
        }


        Participation participation = participationRepository.findById(requestId)
                .orElseThrow(() ->
                        new IntegrityException("Participation request with id=%d not found".formatted(requestId)));


        if (participation.getRequester().getId() != userId) {
            throw new IntegrityException("Request from user id=%d to participation id=%d does not belong to user"
                    .formatted(userId, requestId));
        }


        if (participation.getStatus() != ParticipationStatus.REJECTED) {


            if (participation.getStatus() == ParticipationStatus.CONFIRMED) {
                Event event = participation.getEvent();
                event.decreaseConfirmedRequests();
                eventRepository.save(event);
            }
            participation.setStatus(ParticipationStatus.CANCELED);
            participation = participationRepository.save(participation);
        }

        log.info("Participation request with id={} has been cancelled", requestId);
        return ParticipationMapper.toDto(participation);
    }

    @Override
    public List<ParticipationRequestDto> getEventRequests(long userId, long eventId) {


        User user = checkUser(userId);
        Event event = checkEvent(eventId);
        checkUserOwnsEvent(user, event);

        List<Participation> requests = participationRepository.findAllByEvent(event);
        log.info("Get event participation requests returns {} records", requests.size());
        return ParticipationMapper.toDtos(requests);
    }

    @Override
    public ParticipationStatusUpdateResult updateStatus(long userId, long eventId, ParticipationStatusUpdateRequest updateRequest) {


        User user = checkUser(userId);
        Event event = checkEvent(eventId);
        checkUserOwnsEvent(user, event);


        if (updateRequest.getStatus() == ParticipationStatus.CONFIRMED && event.isLimitExpired()) {
            throw new IntegrityException("Event with id=%d has no available to participate".formatted(eventId));
        }


        List<Participation> requests = (updateRequest.getIds() != null) ?
                participationRepository.findAllByIdIn(updateRequest.getIds()) :
                participationRepository.findAllByEvent(event);


        if (requests.stream().anyMatch(p -> p.getStatus() != ParticipationStatus.PENDING)) {
            throw new IntegrityException("Not all requests have been pending status");
        }


        int oldConfirmedRequests = event.getConfirmedRequests();
        ParticipationStatus newStatus = updateRequest.getStatus();
        for (Participation request : requests) {
            request.setStatus(newStatus);
            if (newStatus == ParticipationStatus.CONFIRMED) {
                event.increaseConfirmedRequests();
                if (event.isLimitExpired())
                    newStatus = ParticipationStatus.REJECTED;
            }
        }


        if (oldConfirmedRequests != event.getConfirmedRequests()) {
            eventRepository.save(event);
        }


        requests = participationRepository.saveAll(requests);


        ParticipationStatusUpdateResult result = new ParticipationStatusUpdateResult();
        result.setConfirmedRequests(requests.stream()
                .filter(p -> p.getStatus() == ParticipationStatus.CONFIRMED)
                .map(ParticipationMapper::toDto)
                .toList());
        result.setRejectedRequests(requests.stream()
                .filter(p -> p.getStatus() == ParticipationStatus.REJECTED)
                .map(ParticipationMapper::toDto)
                .toList());
        log.info("Update participation status request returns {} confirmed and {} rejected records",
                result.getConfirmedRequests().size(), result.getRejectedRequests().size());
        return result;
    }
}