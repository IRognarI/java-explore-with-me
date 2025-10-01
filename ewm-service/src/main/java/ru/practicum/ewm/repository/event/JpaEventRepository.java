package ru.practicum.ewm.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.event.Event;

import java.util.List;

@Repository
public interface JpaEventRepository extends JpaRepository<Event, Long> {

    @Query(value = """
            select *
            from events as ev
            where ev.initiator_id = :userId
            and ev.state_ev = 'PUBLISHED'
            limit :size
            offset :from
            """, nativeQuery = true)
    List<Event> getInitiatorEvent(@Param("from") Integer from,
                                  @Param("size") Integer size,
                                  @Param("userId") Long userId);

    Event getEventByIdAndInitiator_Id(Long eventId, Long userId);
}
