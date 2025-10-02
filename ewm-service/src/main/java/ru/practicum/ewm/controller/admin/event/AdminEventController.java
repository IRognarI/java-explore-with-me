package ru.practicum.ewm.controller.admin.event;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.eventDto.EventDto;
import ru.practicum.ewm.interfaces.event.EventService;
import ru.practicum.ewm.mapper.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventController {
    private static final Logger LOG = LoggerFactory.getLogger(AdminEventController.class);

    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventDto> searchEventsWithParams(
            @RequestParam(name = "users", required = false)
            Long[] userIds,
            @RequestParam(name = "states", required = false)
            String[] states,
            @RequestParam(name = "categories", required = false)
            Long[] categoriesIds,
            @RequestParam(name = "rangeStart", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime rangeStart,
            @RequestParam(name = "rangeEnd", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime rangeEnd,
            @RequestParam(name = "from", required = false)
            Integer from,
            @RequestParam(name = "size", required = false)
            Integer size) {

        LOG.info("Получили GET запрос в endPoint \"/admin/events\" для поиска мероприятия по параметрам:\n" +
                        "ID пользователей: {}\nСостояния: {}\nID категорий: {}\nНачало диапазона поиска: {}" +
                        "\nКонец диапазона: {}\nПри выдаче нужно пропустить: {}\nОграничение на вывод: {}",
                userIds, states, categoriesIds, rangeStart, rangeEnd, from, size);

        return eventService.searchEventsWithParams(userIds, states, categoriesIds, rangeStart, rangeEnd, from, size)
                .stream()
                .map(Mapper::eventToDto)
                .toList();
    }
}
