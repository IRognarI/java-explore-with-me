package ru.practicum.ewmService.compilation.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewmService.compilation.dto.CompilationDto;
import ru.practicum.ewmService.compilation.dto.NewCompilationDto;
import ru.practicum.ewmService.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewmService.compilation.interfaces.CompilationService;
import ru.practicum.ewmService.compilation.mapper.CompilationMapper;
import ru.practicum.ewmService.compilation.model.Compilation;
import ru.practicum.ewmService.compilation.repository.CompilationRepository;
import ru.practicum.ewmService.compiledEvents.model.CompiledEvent;
import ru.practicum.ewmService.compiledEvents.repository.CompiledEventsRepository;
import ru.practicum.ewmService.event.dto.EventShortDto;
import ru.practicum.ewmService.event.mapper.EventMapper;
import ru.practicum.ewmService.event.model.Event;
import ru.practicum.ewmService.event.repository.EventRepository;
import ru.practicum.ewmService.exceptions.IntegrityException;
import ru.practicum.ewmService.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

/**
 * Реализация сервиса для управления подборками событий.
 * Предоставляет методы для создания, обновления, получения и удаления подборок,
 * а также для работы с ассоциированными событиями.
 */
@Slf4j
@Service
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompiledEventsRepository compiledEventsRepository;
    private final EventRepository eventRepository;

    private List<CompilationDto> makeCompilationDto(Collection<Compilation> compList) {

        List<CompiledEvent> compiledEvents = compiledEventsRepository.fetchAllByCompilationIn(compList);

        List<CompilationDto> result = new ArrayList<>();
        for (Compilation compilation : compList) {
            CompilationDto compilationDto = CompilationMapper.toDto(compilation);
            List<EventShortDto> events = compiledEvents.stream()
                    .filter(ce -> Objects.equals(ce.getCompilation(), compilation))
                    .map(CompiledEvent::getEvent)
                    .distinct()
                    .map(EventMapper::toShortDto)
                    .toList();
            compilationDto.setEvents(events);
            result.add(compilationDto);
        }

        return result;
    }

    private Compilation checkCompilation(long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Compilation with id=%d not found".formatted(id)));
    }

    private List<Event> registerEvents(Compilation compilation, Collection<Long> eventIds) {

        List<Event> events = Collections.emptyList();
        compiledEventsRepository.deleteAllByCompilation(compilation);
        if (eventIds != null && !eventIds.isEmpty()) {
            events = eventRepository.findAllByIdIn(eventIds);
            List<CompiledEvent> compiledEvents = events.stream()
                    .map(e -> new CompiledEvent(null, compilation, e))
                    .toList();
            compiledEventsRepository.saveAll(compiledEvents);
        }
        return events;
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {

        Pageable pageable = PageRequest.of(from, size);
        Iterable<Compilation> compilations = (pinned != null) ?
                compilationRepository.findAllByPinned(pinned, pageable) :
                compilationRepository.findAll(pageable);
        List<Compilation> compilationList = StreamSupport.stream(compilations.spliterator(), false).toList();

        List<CompilationDto> result = makeCompilationDto(compilationList);
        log.info("Get compilations service method returns list size: {}", result.size());
        return result;
    }

    @Override
    public CompilationDto getCompilation(long compId) {

        Compilation compilation = checkCompilation(compId);

        CompilationDto compilationDto = makeCompilationDto(List.of(compilation)).getFirst();
        log.info("Get compilation service method returns {}", compilationDto);
        return compilationDto;
    }

    @Override
    public CompilationDto addCompilation(NewCompilationDto dto) {

        if (compilationRepository.existsByTitle(dto.getTitle())) {
            throw new IntegrityException("Compilation with title '%s' already exists".formatted(dto.getTitle()));
        }

        Compilation compilation = CompilationMapper.toCompilation(dto);
        compilation = compilationRepository.save(compilation);
        List<Event> events = registerEvents(compilation, dto.getEvents());

        CompilationDto compilationDto = CompilationMapper.toDto(compilation);
        compilationDto.setEvents(events.stream().map(EventMapper::toShortDto).toList());
        log.info("Add compilation service method returns {}", compilationDto);
        return compilationDto;
    }

    @Override
    public CompilationDto updateCompilation(long compId, UpdateCompilationRequest updateRequest) {

        Compilation compilation = checkCompilation(compId);

        if (updateRequest.getTitle() != null) {
            compilation.setTitle(updateRequest.getTitle());
        }
        if (updateRequest.getPinned() != null) {
            compilation.setPinned(updateRequest.getPinned());
        }
        List<Event> events;
        if (updateRequest.getEvents() != null) {
            events = registerEvents(compilation, updateRequest.getEvents());
        } else {
            events = compiledEventsRepository.getEventsOfCompilation(compilation);
        }

        CompilationDto compilationDto = CompilationMapper.toDto(compilation);
        compilationDto.setEvents(events.stream().map(EventMapper::toShortDto).toList());
        log.info("Update compilation service method returns {}", compilationDto);
        return compilationDto;
    }

    @Override
    public void deleteCompilation(long compId) {

        Compilation compilation = checkCompilation(compId);

        compiledEventsRepository.deleteAllByCompilation(compilation);
        compilationRepository.deleteById(compId);
    }
}