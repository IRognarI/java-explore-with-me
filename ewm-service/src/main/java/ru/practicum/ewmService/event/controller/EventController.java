package ru.practicum.ewmService.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewmService.dateTimeFormatter.Formatter;
import ru.practicum.ewmService.event.dto.EventFullDto;
import ru.practicum.ewmService.event.dto.EventListRequestAdmin;
import ru.practicum.ewmService.event.dto.EventListRequestPublic;
import ru.practicum.ewmService.event.dto.EventShortDto;
import ru.practicum.ewmService.event.dto.NewEventDto;
import ru.practicum.ewmService.event.dto.UpdateEventRequest;
import ru.practicum.ewmService.event.enums.EventState;
import ru.practicum.ewmService.event.enums.SortingMode;
import ru.practicum.ewmService.event.interfaces.EventService;
import ru.practicum.statsClient.StatsClient;
import ru.practicum.statsDto.StatsItemDto;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Контроллер для управления событиями.
 * Предоставляет конечные точки для создания, обновления и получения событий,
 * как для администраторов, так и для обычных пользователей.
 */
@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping
public class EventController {

    private final EventService eventService;
    private final StatsClient statsClient;

    @GetMapping("/admin/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getEvents(@RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) List<EventState> states,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = Formatter.PATTERN) LocalDateTime rangeStart,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = Formatter.PATTERN) LocalDateTime rangeEnd,
                                        @RequestParam(required = false, defaultValue = "0") int from,
                                        @RequestParam(required = false, defaultValue = "10") int size) {

        var request = new EventListRequestAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
        log.info("Get events admin GET request: {}", request);
        return eventService.getEventsAdmin(request);
    }

    @PatchMapping("/admin/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable long eventId, @Valid @RequestBody UpdateEventRequest updateRequest) {

        log.info("Update event admin PATCH request: {}", updateRequest);
        return eventService.updateEventAdmin(eventId, updateRequest);
    }

    private void postStatistics(HttpServletRequest request) {

        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        log.info("Update statistics for ip={} and uri={}", ip, uri);
        statsClient.addHit("ewm-main-server", uri, ip, LocalDateTime.now());
    }

    private void getStatistics(EventFullDto dto) {

        String uri = "/events/" + dto.getId();
        log.info("Get statistics for uri={}", uri);
        List<StatsItemDto> listDto = statsClient.getStats(
                LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0),
                LocalDateTime.of(2035, Month.DECEMBER, 31, 23, 59),
                List.of(uri),
                true);
        if (listDto != null && !listDto.isEmpty()) {
            dto.setViews(listDto.getFirst().getHits());
        }
    }

    private void getStatistics(Collection<EventShortDto> dtos) {

        Map<String, EventShortDto> dtoMap = dtos.stream().collect(Collectors.toMap(
                e -> "/events/" + e.getId(),
                e -> e));
        log.info("Get statistics for {} uris", dtoMap.size());
        log.debug("Get statistics for uris: {}", dtoMap.keySet());
        List<StatsItemDto> listDto = statsClient.getStats(
                LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0),
                LocalDateTime.of(2035, Month.DECEMBER, 31, 23, 59),
                List.copyOf(dtoMap.keySet()),
                true);
        if (listDto != null && !listDto.isEmpty()) {
            listDto.forEach(statsItem -> {
                var dto = dtoMap.get(statsItem.getUri());
                if (dto != null) {
                    dto.setViews(statsItem.getHits());
                }
            });
        }
    }

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = Formatter.PATTERN) LocalDateTime rangeStart,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = Formatter.PATTERN) LocalDateTime rangeEnd,
                                         @RequestParam(required = false, defaultValue = "FALSE") Boolean onlyAvailable,
                                         @RequestParam(required = false) SortingMode sort,
                                         @RequestParam(required = false, defaultValue = "0") int from,
                                         @RequestParam(required = false, defaultValue = "10") int size,
                                         HttpServletRequest request) {

        postStatistics(request);

        var dto = new EventListRequestPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        log.info("Get events public GET request: {}", dto);
        List<EventShortDto> dtos = eventService.getEventsPublic(dto);

        getStatistics(dtos);
        return dtos;
    }

    @GetMapping("/events/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEvent(@PathVariable long id, HttpServletRequest request) {

        postStatistics(request);

        log.info("Get event with id={} public GET request", id);
        EventFullDto dto = eventService.getEventPublic(id);

        getStatistics(dto);
        return dto;
    }

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId, @Valid @RequestBody NewEventDto eventDto) {

        log.info("New event private POST request: {}", eventDto.getTitle());
        return eventService.addEvent(userId, eventDto);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable long userId,
                                    @PathVariable long eventId,
                                    @Valid @RequestBody UpdateEventRequest updateRequest) {

        log.info("Update event private PATCH request: userId={}, eventId={}", userId, eventId);
        return eventService.updateEventPrivate(userId, eventId, updateRequest);
    }

    @GetMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEvents(@PathVariable long userId,
                                         @RequestParam(required = false, defaultValue = "0") int from,
                                         @RequestParam(required = false, defaultValue = "10") int size) {

        log.info("Get events private GET request: userId={}, from={}, size={}", userId, from, size);
        return eventService.getEventsPrivate(userId, from, size);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEvent(@PathVariable long userId, @PathVariable long eventId) {

        log.info("Get event private GET request: userId={}, eventId={}", userId, eventId);
        return eventService.getEventPrivate(userId, eventId);
    }
}