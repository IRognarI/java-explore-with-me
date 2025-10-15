package ru.practicum.ewmService.participation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewmService.event.enums.EventState;
import ru.practicum.ewmService.event.model.Event;
import ru.practicum.ewmService.event.repository.EventRepository;
import ru.practicum.ewmService.exceptions.IntegrityException;
import ru.practicum.ewmService.exceptions.NotFoundException;
import ru.practicum.ewmService.participation.dto.ParticipationRequestDto;
import ru.practicum.ewmService.participation.enums.ParticipationStatus;
import ru.practicum.ewmService.participation.model.Participation;
import ru.practicum.ewmService.participation.repository.ParticipationRepository;
import ru.practicum.ewmService.user.model.User;
import ru.practicum.ewmService.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParticipationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ParticipationRepository participationRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private ParticipationServiceImpl participationService;

    private User user;
    private Event event;
    private Participation participation;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");

        event = new Event();
        event.setId(1L);
        event.setInitiator(user);
        event.setState(EventState.PUBLISHED);
        event.setParticipantLimit(10);
        event.setRequestModeration(true);
        event.setConfirmedRequests(0);

        participation = new Participation();
        participation.setId(1L);
        participation.setRequester(user);
        participation.setEvent(event);
        participation.setCreated(LocalDateTime.now());
        participation.setStatus(ParticipationStatus.PENDING);
    }

    @Test
    void getUserRequests_shouldReturnListOfParticipationRequestDtos() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(participationRepository.findAllByRequester(any(User.class))).thenReturn(List.of(participation));

        List<ParticipationRequestDto> result = participationService.getUserRequests(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findById(1L);
        verify(participationRepository).findAllByRequester(user);
    }

    @Test
    void getUserRequests_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> participationService.getUserRequests(1L));
        verify(userRepository).findById(1L);
    }

    @Test
    void addRequest_shouldCreatePendingParticipationRequest_whenModerationRequired() {
        User requester = new User();
        requester.setId(2L);
        Event eventWithLimit = new Event();
        eventWithLimit.setId(1L);
        eventWithLimit.setInitiator(user);
        eventWithLimit.setState(EventState.PUBLISHED);
        eventWithLimit.setParticipantLimit(10);
        eventWithLimit.setRequestModeration(true);
        eventWithLimit.setConfirmedRequests(0);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requester));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(eventWithLimit));
        when(participationRepository.existsByRequesterAndEvent(any(User.class), any(Event.class))).thenReturn(false);
        when(participationRepository.save(any(Participation.class))).thenReturn(participation);

        ParticipationRequestDto result = participationService.addRequest(2L, 1L);

        assertNotNull(result);
        assertEquals(ParticipationStatus.PENDING.name(), result.getStatus());
        verify(participationRepository).save(any(Participation.class));
    }

    @Test
    void addRequest_shouldCreateConfirmedParticipationRequest_whenNoModerationRequired() {
        User requester = new User();
        requester.setId(2L);
        Event eventWithoutLimit = new Event();
        eventWithoutLimit.setId(1L);
        eventWithoutLimit.setInitiator(user);
        eventWithoutLimit.setState(EventState.PUBLISHED);
        eventWithoutLimit.setParticipantLimit(0);
        eventWithoutLimit.setRequestModeration(false);
        eventWithoutLimit.setConfirmedRequests(0);

        Participation confirmedParticipation = new Participation();
        confirmedParticipation.setId(1L);
        confirmedParticipation.setRequester(requester);
        confirmedParticipation.setEvent(eventWithoutLimit);
        confirmedParticipation.setCreated(LocalDateTime.now());
        confirmedParticipation.setStatus(ParticipationStatus.CONFIRMED);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requester));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(eventWithoutLimit));
        when(participationRepository.existsByRequesterAndEvent(any(User.class), any(Event.class))).thenReturn(false);
        when(participationRepository.save(any(Participation.class))).thenReturn(confirmedParticipation);

        ParticipationRequestDto result = participationService.addRequest(2L, 1L);

        assertNotNull(result);
        assertEquals(ParticipationStatus.CONFIRMED.name(), result.getStatus());
        verify(participationRepository).save(any(Participation.class));
        verify(eventRepository).save(eventWithoutLimit);
    }

    @Test
    void addRequest_shouldThrowIntegrityException_whenRequestAlreadyExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(participationRepository.existsByRequesterAndEvent(any(User.class), any(Event.class))).thenReturn(true);

        assertThrows(IntegrityException.class, () -> participationService.addRequest(1L, 1L));
    }

    @Test
    void addRequest_shouldThrowIntegrityException_whenUserOwnsEvent() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));

        assertThrows(IntegrityException.class, () -> participationService.addRequest(1L, 1L));
    }

    @Test
    void addRequest_shouldThrowIntegrityException_whenEventNotPublished() {
        User requester = new User();
        requester.setId(2L);
        Event unpublishedEvent = new Event();
        unpublishedEvent.setId(1L);
        unpublishedEvent.setInitiator(user);
        unpublishedEvent.setState(EventState.PENDING);
        unpublishedEvent.setParticipantLimit(10);
        unpublishedEvent.setRequestModeration(true);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requester));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(unpublishedEvent));

        assertThrows(IntegrityException.class, () -> participationService.addRequest(2L, 1L));
    }

    @Test
    void addRequest_shouldThrowIntegrityException_whenEventLimitExpired() {
        User requester = new User();
        requester.setId(2L);
        Event fullEvent = new Event();
        fullEvent.setId(1L);
        fullEvent.setInitiator(user);
        fullEvent.setState(EventState.PUBLISHED);
        fullEvent.setParticipantLimit(10);
        fullEvent.setRequestModeration(true);
        fullEvent.setConfirmedRequests(10);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requester));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(fullEvent));

        assertThrows(IntegrityException.class, () -> participationService.addRequest(2L, 1L));
    }

    @Test
    void cancelRequest_shouldCancelPendingRequest() {
        participation.setStatus(ParticipationStatus.PENDING);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(participationRepository.findById(anyLong())).thenReturn(Optional.of(participation));
        when(participationRepository.save(any(Participation.class))).thenReturn(participation);

        ParticipationRequestDto result = participationService.cancelRequest(1L, 1L);

        assertNotNull(result);
        assertEquals(ParticipationStatus.CANCELED.name(), result.getStatus());
        verify(participationRepository).save(participation);
    }

    @Test
    void cancelRequest_shouldCancelConfirmedRequest_andDecreaseEventCounter() {
        participation.setStatus(ParticipationStatus.CONFIRMED);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(participationRepository.findById(anyLong())).thenReturn(Optional.of(participation));
        when(participationRepository.save(any(Participation.class))).thenReturn(participation);

        ParticipationRequestDto result = participationService.cancelRequest(1L, 1L);

        assertNotNull(result);
        assertEquals(ParticipationStatus.CANCELED.name(), result.getStatus());
        assertEquals(-1, event.getConfirmedRequests());
        verify(participationRepository).save(participation);
        verify(eventRepository).save(event);
    }

    @Test
    void cancelRequest_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> participationService.cancelRequest(1L, 1L));
    }

    @Test
    void cancelRequest_shouldThrowIntegrityException_whenParticipationNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(participationRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IntegrityException.class, () -> participationService.cancelRequest(1L, 1L));
    }

    @Test
    void cancelRequest_shouldThrowIntegrityException_whenRequestDoesNotBelongToUser() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        participation.setRequester(anotherUser);

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(participationRepository.findById(anyLong())).thenReturn(Optional.of(participation));

        assertThrows(IntegrityException.class, () -> participationService.cancelRequest(1L, 1L));
    }

    @Test
    void cancelRequest_shouldNotChangeStatus_whenAlreadyRejected() {
        participation.setStatus(ParticipationStatus.REJECTED);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(participationRepository.findById(anyLong())).thenReturn(Optional.of(participation));

        ParticipationRequestDto result = participationService.cancelRequest(1L, 1L);

        assertNotNull(result);
        assertEquals(ParticipationStatus.REJECTED.name(), result.getStatus());
        verify(participationRepository, never()).save(any(Participation.class));
    }
}