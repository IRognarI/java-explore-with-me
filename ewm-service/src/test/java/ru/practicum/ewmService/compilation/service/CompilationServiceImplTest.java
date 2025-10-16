package ru.practicum.ewmService.compilation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewmService.category.model.Category;
import ru.practicum.ewmService.compilation.dto.CompilationDto;
import ru.practicum.ewmService.compilation.dto.NewCompilationDto;
import ru.practicum.ewmService.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewmService.compilation.model.Compilation;
import ru.practicum.ewmService.compilation.repository.CompilationRepository;
import ru.practicum.ewmService.compiledEvents.model.CompiledEvent;
import ru.practicum.ewmService.compiledEvents.repository.CompiledEventsRepository;
import ru.practicum.ewmService.event.model.Event;
import ru.practicum.ewmService.event.repository.EventRepository;
import ru.practicum.ewmService.exceptions.IntegrityException;
import ru.practicum.ewmService.exceptions.NotFoundException;
import ru.practicum.ewmService.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompilationServiceImplTest {

    @Mock
    private CompilationRepository compilationRepository;

    @Mock
    private CompiledEventsRepository compiledEventsRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private CompilationServiceImpl compilationService;

    private Event createEvent(Long id) {
        Event event = new Event();
        event.setId(id);
        event.setTitle("Event " + id);
        event.setAnnotation("Annotation " + id);

        Category category = new Category();
        category.setId(1L);
        category.setName("Category");
        event.setCategory(category);

        User user = new User();
        user.setId(1L);
        user.setName("User");
        event.setInitiator(user);

        event.setEventDate(LocalDateTime.now().plusDays(1));
        event.setPaid(false);
        event.setConfirmedRequests(0);

        return event;
    }

    @Test
    void getCompilations_whenPinnedIsNull_thenReturnAllCompilations() {
        Compilation compilation = new Compilation();
        compilation.setId(1L);
        compilation.setTitle("Test Compilation");
        compilation.setPinned(false);

        List<Compilation> compilations = List.of(compilation);
        PageImpl<Compilation> page = new PageImpl<>(compilations);

        when(compilationRepository.findAll(any(Pageable.class))).thenReturn(page);

        CompiledEvent compiledEvent = new CompiledEvent();
        compiledEvent.setCompilation(compilation);
        Event event = createEvent(1L);
        compiledEvent.setEvent(event);

        when(compiledEventsRepository.fetchAllByCompilationIn(compilations)).thenReturn(List.of(compiledEvent));

        List<CompilationDto> result = compilationService.getCompilations(null, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Compilation", result.get(0).getTitle());
        assertFalse(result.get(0).isPinned());
        verify(compilationRepository, times(1)).findAll(PageRequest.of(0, 10));
    }

    @Test
    void getCompilations_whenPinnedIsTrue_thenReturnPinnedCompilations() {
        Compilation compilation = new Compilation();
        compilation.setId(1L);
        compilation.setTitle("Pinned Compilation");
        compilation.setPinned(true);

        List<Compilation> compilations = List.of(compilation);
        PageImpl<Compilation> page = new PageImpl<>(compilations);

        when(compilationRepository.findAllByPinned(true, PageRequest.of(0, 10))).thenReturn(page);

        CompiledEvent compiledEvent = new CompiledEvent();
        compiledEvent.setCompilation(compilation);
        Event event = createEvent(1L);
        compiledEvent.setEvent(event);

        when(compiledEventsRepository.fetchAllByCompilationIn(compilations)).thenReturn(List.of(compiledEvent));

        List<CompilationDto> result = compilationService.getCompilations(true, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isPinned());
        verify(compilationRepository, times(1)).findAllByPinned(true, PageRequest.of(0, 10));
    }

    @Test
    void getCompilation_whenCompilationExists_thenReturnCompilationDto() {
        Compilation compilation = new Compilation();
        compilation.setId(1L);
        compilation.setTitle("Test Compilation");
        compilation.setPinned(true);

        when(compilationRepository.findById(1L)).thenReturn(Optional.of(compilation));

        CompiledEvent compiledEvent = new CompiledEvent();
        compiledEvent.setCompilation(compilation);
        Event event = createEvent(1L);
        compiledEvent.setEvent(event);

        when(compiledEventsRepository.fetchAllByCompilationIn(List.of(compilation))).thenReturn(List.of(compiledEvent));

        CompilationDto result = compilationService.getCompilation(1L);

        assertNotNull(result);
        assertEquals("Test Compilation", result.getTitle());
        assertTrue(result.isPinned());
        verify(compilationRepository, times(1)).findById(1L);
    }

    @Test
    void getCompilation_whenCompilationNotExists_thenThrowNotFoundException() {
        when(compilationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> compilationService.getCompilation(1L));
        verify(compilationRepository, times(1)).findById(1L);
    }

    @Test
    void addCompilation_whenTitleIsUnique_thenReturnCompilationDto() {
        NewCompilationDto dto = new NewCompilationDto();
        dto.setTitle("New Compilation");
        dto.setPinned(true);
        dto.setEvents(List.of(1L, 2L));

        Compilation compilation = new Compilation();
        compilation.setId(1L);
        compilation.setTitle("New Compilation");
        compilation.setPinned(true);

        Event event1 = createEvent(1L);
        Event event2 = createEvent(2L);
        List<Event> events = List.of(event1, event2);

        when(compilationRepository.existsByTitle("New Compilation")).thenReturn(false);
        when(compilationRepository.save(any(Compilation.class))).thenReturn(compilation);
        when(eventRepository.findAllByIdIn(List.of(1L, 2L))).thenReturn(events);

        when(compiledEventsRepository.saveAll(anyList())).thenReturn(new ArrayList<>());

        CompilationDto result = compilationService.addCompilation(dto);

        assertNotNull(result);
        assertEquals("New Compilation", result.getTitle());
        assertTrue(result.isPinned());
        assertEquals(2, result.getEvents().size());
        verify(compilationRepository, times(1)).existsByTitle("New Compilation");
        verify(compilationRepository, times(1)).save(any(Compilation.class));
        verify(eventRepository, times(1)).findAllByIdIn(List.of(1L, 2L));
        verify(compiledEventsRepository, times(1)).saveAll(anyList());
    }

    @Test
    void addCompilation_whenTitleAlreadyExists_thenThrowIntegrityException() {
        NewCompilationDto dto = new NewCompilationDto();
        dto.setTitle("Existing Compilation");
        dto.setPinned(false);
        dto.setEvents(new ArrayList<>());

        when(compilationRepository.existsByTitle("Existing Compilation")).thenReturn(true);

        assertThrows(IntegrityException.class, () -> compilationService.addCompilation(dto));
        verify(compilationRepository, times(1)).existsByTitle("Existing Compilation");
        verify(compilationRepository, never()).save(any(Compilation.class));
    }

    @Test
    void updateCompilation_whenCompilationExistsAndEventsProvided_thenReturnUpdatedCompilationDto() {
        UpdateCompilationRequest updateRequest = new UpdateCompilationRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setPinned(false);
        updateRequest.setEvents(List.of(1L, 2L));

        Compilation compilation = new Compilation();
        compilation.setId(1L);
        compilation.setTitle("Old Title");
        compilation.setPinned(true);

        Event event1 = createEvent(1L);
        Event event2 = createEvent(2L);
        List<Event> events = List.of(event1, event2);

        when(compilationRepository.findById(1L)).thenReturn(Optional.of(compilation));
        when(eventRepository.findAllByIdIn(List.of(1L, 2L))).thenReturn(events);
        when(compiledEventsRepository.saveAll(anyList())).thenReturn(new ArrayList<>());

        CompilationDto result = compilationService.updateCompilation(1L, updateRequest);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertFalse(result.isPinned());
        verify(compilationRepository, times(1)).findById(1L);
        verify(eventRepository, times(1)).findAllByIdIn(List.of(1L, 2L));
        verify(compiledEventsRepository, times(1)).saveAll(anyList());
    }

    @Test
    void updateCompilation_whenCompilationExistsAndNoEventsProvided_thenReturnCompilationDtoWithExistingEvents() {
        UpdateCompilationRequest updateRequest = new UpdateCompilationRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setPinned(false);
        updateRequest.setEvents(null);

        Compilation compilation = new Compilation();
        compilation.setId(1L);
        compilation.setTitle("Old Title");
        compilation.setPinned(true);

        Event event1 = createEvent(1L);
        Event event2 = createEvent(2L);
        List<Event> events = List.of(event1, event2);

        when(compilationRepository.findById(1L)).thenReturn(Optional.of(compilation));
        when(compiledEventsRepository.getEventsOfCompilation(compilation)).thenReturn(events);

        CompilationDto result = compilationService.updateCompilation(1L, updateRequest);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertFalse(result.isPinned());
        verify(compilationRepository, times(1)).findById(1L);
        verify(compiledEventsRepository, times(1)).getEventsOfCompilation(compilation);
    }

    @Test
    void deleteCompilation_whenCompilationExists_thenDeleteCompilationAndRelatedEvents() {
        Compilation compilation = new Compilation();
        compilation.setId(1L);

        when(compilationRepository.findById(1L)).thenReturn(Optional.of(compilation));

        compilationService.deleteCompilation(1L);

        verify(compilationRepository, times(1)).findById(1L);
        verify(compiledEventsRepository, times(1)).deleteAllByCompilation(compilation);
        verify(compilationRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteCompilation_whenCompilationNotExists_thenThrowNotFoundException() {
        when(compilationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> compilationService.deleteCompilation(1L));
        verify(compilationRepository, times(1)).findById(1L);
        verify(compiledEventsRepository, never()).deleteAllByCompilation(any());
        verify(compilationRepository, never()).deleteById(anyLong());
    }
}