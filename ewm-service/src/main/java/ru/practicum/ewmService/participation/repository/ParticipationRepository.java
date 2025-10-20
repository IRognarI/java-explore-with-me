package ru.practicum.ewmService.participation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmService.event.model.Event;
import ru.practicum.ewmService.participation.enums.ParticipationStatus;
import ru.practicum.ewmService.participation.model.Participation;
import ru.practicum.ewmService.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {


    List<Participation> findAllByRequester(User requester);

    boolean existsByRequesterAndEvent(User requester, Event event);

    List<Participation> findAllByEvent(Event event);

    List<Participation> findAllByIdIn(Collection<Long> ids);

    boolean existsByRequesterId(Long requesterId);

    Optional<Participation> findByEventAndRequester(Event event, User requester);

    boolean existsByEventAndRequesterAndStatus(Event event, User user, ParticipationStatus participationStatus);
}