package ru.practicum.ewmService.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmService.category.model.Category;
import ru.practicum.ewmService.category.repository.CategoryRepository;
import ru.practicum.ewmService.event.dto.EventFullDto;
import ru.practicum.ewmService.event.dto.EventListRequestAdmin;
import ru.practicum.ewmService.event.dto.EventListRequestPublic;
import ru.practicum.ewmService.event.dto.EventShortDto;
import ru.practicum.ewmService.event.dto.NewEventDto;
import ru.practicum.ewmService.event.dto.UpdateEventRequest;
import ru.practicum.ewmService.event.enums.EventState;
import ru.practicum.ewmService.event.interfaces.EventService;
import ru.practicum.ewmService.event.mapper.EventMapper;
import ru.practicum.ewmService.event.model.Event;
import ru.practicum.ewmService.event.model.QEvent;
import ru.practicum.ewmService.event.repository.EventRepository;
import ru.practicum.ewmService.exceptions.IntegrityException;
import ru.practicum.ewmService.exceptions.IsBadRequestException;
import ru.practicum.ewmService.exceptions.NotFoundException;
import ru.practicum.ewmService.user.model.User;
import ru.practicum.ewmService.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Реализация сервиса для управления событиями.
 * Предоставляет методы для создания, обновления и получения событий,
 * как для частных пользователей, так и для публичного/административного доступа.
 */
@Slf4j
@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    private Event checkEvent(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=%d not found".formatted(eventId)));
    }


    private void updateEvent(Event event, UpdateEventRequest updateRequest) {

        if (updateRequest.getAnnotation() != null) {
            event.setAnnotation(updateRequest.getAnnotation());
        }
        if (updateRequest.getCategory() != null &&
                !Objects.equals(updateRequest.getCategory(), event.getCategory().getId())) {

            Category newCategory = categoryRepository.findById(updateRequest.getCategory()).orElseThrow(
                    () -> new NotFoundException("Category with id=%d not found".formatted(updateRequest.getCategory())));
            event.setCategory(newCategory);
        }
        if (updateRequest.getDescription() != null) {
            event.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getEventDate() != null) {

            LocalDateTime minTime = LocalDateTime.now().plusHours(2);
            if (updateRequest.getEventDate().isBefore(minTime)) {
                throw new IntegrityException("Event date is before min time");
            }
            event.setEventDate(updateRequest.getEventDate());
        }
        if (updateRequest.getLocation() != null) {
            event.setLocation(updateRequest.getLocation());
        }
        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }
        if (updateRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }
        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }
        if (updateRequest.getTitle() != null) {
            event.setTitle(updateRequest.getTitle());
        }
    }

    private void checkRangesDateTime(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new IsBadRequestException("Range start date is after end date");
        }
    }


    @Override
    public EventFullDto addEvent(long userId, NewEventDto dto) {


        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id=%d not found".formatted(userId)));

        Category category = categoryRepository.findById(dto.getCategory()).orElseThrow(
                () -> new NotFoundException("Category with id=%d not found".formatted(dto.getCategory())));

        LocalDateTime minTime = LocalDateTime.now().plusHours(2);
        if (dto.getEventDate().isBefore(minTime)) {
            throw new IntegrityException("Event date is before min time");
        }
        Event event = EventMapper.toEvent(dto);
        event.setInitiator(user);
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
        event = eventRepository.save(event);
        log.info("Added event: id={}, title={}", event.getId(), event.getTitle());
        return EventMapper.toFullDto(event);
    }

    @Override
    public EventFullDto updateEventPrivate(long userId, long eventId, UpdateEventRequest updateRequest) {


        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id=%d not found".formatted(userId)));

        Event event = checkEvent(eventId);
        if (event.getState() == EventState.PUBLISHED) {
            throw new IntegrityException("Only pending or canceled events can be changed");
        }


        if (!Objects.equals(event.getInitiator(), user)) {
            throw new IntegrityException("Initiators don't match");
        }


        updateEvent(event, updateRequest);
        if (updateRequest.getStateAction() != null) {
            switch (updateRequest.getStateAction()) {
                case SEND_TO_REVIEW -> event.setState(EventState.PENDING);
                case CANCEL_REVIEW -> event.setState(EventState.CANCELED);
                default ->
                        throw new IntegrityException("Only send-review or cancel-receive event state action are possible");
            }
            event.setPublishedOn(LocalDateTime.now());
        }


        event = eventRepository.save(event);
        log.info("Updated event: id={}, title={}", event.getId(), event.getTitle());
        return EventMapper.toFullDto(event);
    }

    @Override
    public List<EventShortDto> getEventsPrivate(long userId, int from, int size) {

        List<Event> events = eventRepository.findByInitiatorId(userId, PageRequest.of(from, size));
        log.info("Getting events of user id={} returns list of size {}", userId, size);
        return events.stream().map(EventMapper::toShortDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventPrivate(long userId, long eventId) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=%d not found".formatted(userId));
        }
        Event event = checkEvent(eventId);
        if (event.getInitiator().getId() != userId) {
            throw new NotFoundException("Event id=%d owns user id=%d not found".formatted(eventId, userId));
        }
        EventFullDto eventFullDto = EventMapper.toFullDto(event);
        log.info("Private get event request returns event: {}", eventFullDto);
        return eventFullDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEventsAdmin(EventListRequestAdmin request) {


        checkRangesDateTime(request.rangeStart(), request.rangeEnd());

        QEvent event = QEvent.event;
        List<BooleanExpression> conditions = new ArrayList<>();

        if (request.users() != null) {
            conditions.add(event.initiator.id.in(request.users()));
        }

        if (request.states() != null) {
            conditions.add(event.state.in(request.states()));
        }

        if (request.categories() != null) {
            conditions.add(event.category.id.in(request.categories()));
        }

        if (request.rangeStart() != null) {
            conditions.add(event.eventDate.after(request.rangeStart()));
        }

        if (request.rangeEnd() != null) {
            conditions.add(event.eventDate.before(request.rangeEnd()));
        }
        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .orElse(null);


        PageRequest pageRequest = PageRequest.of(request.from(), request.size());

        Iterable<Event> events = (finalCondition != null) ?
                eventRepository.findAll(finalCondition, pageRequest) :
                eventRepository.findAll(pageRequest);

        List<EventFullDto> eventFullDtos = StreamSupport
                .stream(events.spliterator(), false)
                .map(EventMapper::toFullDto)
                .toList();
        log.info("Admin getting events returns list of size {}", eventFullDtos.size());
        return eventFullDtos;
    }

    @Override
    public EventFullDto updateEventAdmin(long eventId, UpdateEventRequest updateRequest) {


        Event event = checkEvent(eventId);


        if (updateRequest.getStateAction() != null) {


            if (event.getState() != EventState.PENDING) {
                throw new IntegrityException("Only pending events can be published or cancelled");
            }

            switch (updateRequest.getStateAction()) {
                case PUBLISH_EVENT -> {
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                }
                case REJECT_EVENT -> event.setState(EventState.CANCELED);
                default -> throw new IntegrityException("Only publish-event or reject-event state action are possible");
            }
        }


        updateEvent(event, updateRequest);


        event = eventRepository.save(event);
        log.info("Event was updated by admin: {}", event);
        return EventMapper.toFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsPublic(EventListRequestPublic request) {


        checkRangesDateTime(request.rangeStart(), request.rangeEnd());

        QEvent event = QEvent.event;
        List<BooleanExpression> conditions = new ArrayList<>();
        conditions.add(event.state.eq(EventState.PUBLISHED));


        if (request.text() != null) {
            conditions.add(event.annotation.containsIgnoreCase(request.text()).or(
                    event.description.containsIgnoreCase(request.text())));
        }

        if (request.categories() != null) {
            conditions.add(event.category.id.in(request.categories()));
        }

        if (request.paid() != null) {
            conditions.add(event.paid.eq(request.paid()));
        }

        if (request.rangeStart() != null) {
            conditions.add(event.eventDate.after(request.rangeStart()));
        }

        if (request.rangeEnd() != null) {
            conditions.add(event.eventDate.before(request.rangeEnd()));
        }


        if (request.rangeStart() == null && request.rangeEnd() == null) {
            conditions.add(event.eventDate.after(LocalDateTime.now()));
        }

        if (request.onlyAvailable() != null && request.onlyAvailable()) {
            conditions.add(event.participantLimit.eq(0)
                    .or(event.participantLimit.gt(event.confirmedRequests)));
        }

        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .orElse(null);


        Sort sort = null;
        if (request.sortingMode() != null) {
            switch (request.sortingMode()) {
                case EVENT_DATE -> sort = Sort.by(Sort.Direction.ASC, "eventDate");
                case VIEWS -> sort = Sort.by(Sort.Direction.DESC, "views");
                case RATINGS -> sort = Sort.by(Sort.Direction.DESC, "rating");
            }
        }


        PageRequest pageRequest = (sort != null) ?
                PageRequest.of(request.from(), request.size(), sort) :
                PageRequest.of(request.from(), request.size());

        Iterable<Event> events = eventRepository.findAll(finalCondition, pageRequest);

        List<EventShortDto> dtos = StreamSupport
                .stream(events.spliterator(), false)
                .map(EventMapper::toShortDto)
                .toList();
        log.info("Public getting events returns list of size {}", dtos.size());
        return dtos;
    }

    @Override
    public EventFullDto getEventPublic(long id) {

        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() ->
                        new NotFoundException("Published event with id=%d not found".formatted(id)));

        return EventMapper.toFullDto(event);
    }
}