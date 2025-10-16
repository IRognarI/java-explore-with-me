package ru.practicum.ewmService.compiledEvents.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmService.compilation.model.Compilation;
import ru.practicum.ewmService.compiledEvents.model.CompiledEvent;
import ru.practicum.ewmService.event.model.Event;

import java.util.Collection;
import java.util.List;

@Repository
public interface CompiledEventsRepository extends JpaRepository<CompiledEvent, Long> {

    @Query("""
            select c.event
            from compiled_events as c
            where c.compilation = :compilation
            """)
    List<Event> getEventsOfCompilation(Compilation compilation);

    List<CompiledEvent> findAllByCompilationIn(Collection<Compilation> compilations);

    @Query("""
            select ce from compiled_events ce
            join fetch events e on ce.event.id = e.id
            join fetch compilations c on ce.compilation.id = c.id
            where ce.compilation in :compilations
            """)
    List<CompiledEvent> fetchAllByCompilationIn(Collection<Compilation> compilations);

    void deleteAllByCompilation(Compilation compilation);
}