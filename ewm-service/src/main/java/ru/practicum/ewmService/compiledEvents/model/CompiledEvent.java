package ru.practicum.ewmService.compiledEvents.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewmService.compilation.model.Compilation;
import ru.practicum.ewmService.event.model.Event;

/**
 * Представляет сущность скомпилированного события, которая связывает подборку с событием.
 * Эта сущность используется для установления связи многие-к-одному между подборками и событиями,
 * позволяя группировать события в различные подборки.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "compiled_events")
public class CompiledEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "compilation_id")
    private Compilation compilation;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
}