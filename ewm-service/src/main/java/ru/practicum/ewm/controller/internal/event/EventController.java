package ru.practicum.ewm.controller.internal.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.eventDto.EventDto;
import ru.practicum.dto.eventDto.eventDtoRequest.EventDtoRequest;
import ru.practicum.ewm.interfaces.event.EventService;
import ru.practicum.ewm.mapper.Mapper;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class EventController {
    private static final Logger LOG = LoggerFactory.getLogger(EventController.class);

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto addEvent(@RequestBody @Valid EventDtoRequest eventDtoRequest,
                             @PathVariable(name = "userId") Long userId) {

        LOG.info("Получили POST запрос в endPoint \"/users/{userId}/events\"" +
                " для создания нового мероприятия: {}\nОт пользователя с ID {}", eventDtoRequest, userId);

        return Mapper.eventToDto(eventService.addEvent(eventDtoRequest, userId));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventDto> getEvents(@RequestParam(name = "from") Integer from,
                                    @RequestParam(name = "size") Integer size,
                                    @PathVariable(name = "userId") Long userId) {

        LOG.info("Получили GET запрос в endPoint \"/users/{userId}/events\" на получение мероприятий пользователя с ID {}\n" +
                "Количество позиций для пропуска: {}\nОграничение на выдачу: {}", userId, from, size);

        return eventService.getEvents(from, size, userId).stream()
                .map(Mapper::eventToDto)
                .toList();
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto getTargetEvent(@PathVariable(name = "userId") Long userId,
                                   @PathVariable(name = "eventId") Long eventId) {

        LOG.info("Приняли GET запрос в endPoint \"/users/{userId}/events/{eventId}\" для получения информации о" +
                " мероприятии с ID: {}, от пользователя с ID {}", eventId, userId);

        return Mapper.eventToDto(eventService.getTargetEvent(userId, eventId));
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto updateEvent(@PathVariable(name = "userId") Long userId,
                                @PathVariable(name = "eventId") Long eventId,
                                @RequestBody EventDtoRequest eventDtoRequest) {

        LOG.info("Получили PATCH запрос в endPoint \"/users/{userId}/events/{eventId}\" для обновления мероприятия" +
                "с ID=" + eventId + ", от пользователя с ID=" + userId + ", данные для обновления: " + eventDtoRequest);

        return Mapper.eventToDto(eventService.updateEvent(userId, eventId, eventDtoRequest));
    }
}
