package ru.practicum.ewmService.event.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmService.event.enums.EventState;
import ru.practicum.ewmService.event.model.Event;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    boolean existsByCategoryId(long catId);

    boolean existsByInitiatorId(long userId);

    List<Event> findByInitiatorId(Long initiatorId, PageRequest pageRequest);

    Optional<Event> findByIdAndState(long id, EventState state);

    List<Event> findAllByIdIn(Collection<Long> ids);
}