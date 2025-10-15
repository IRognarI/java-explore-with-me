package ru.practicum.ewmService.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.ewmService.dateTimeFormatter.Formatter;
import ru.practicum.ewmService.event.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Запись, представляющая параметры запроса для получения списка событий администратором.
 * Включает необязательные фильтры по пользователям, состояниям событий, категориям и диапазону дат,
 * а также параметры пагинации.
 *
 * @param users       список ID пользователей для фильтрации событий по инициаторам
 * @param states      список состояний событий для фильтрации
 * @param categories  список ID категорий для фильтрации событий по категориям
 * @param rangeStart  начальная дата и время диапазона для фильтрации событий по дате
 * @param rangeEnd    конечная дата и время диапазона для фильтрации событий по дате
 * @param from        смещение для пагинации (начальный индекс)
 * @param size        количество событий для получения (размер страницы)
 */
public record EventListRequestAdmin(
        List<Long> users,
        List<EventState> states,
        List<Long> categories,

        @JsonFormat(pattern = Formatter.PATTERN)
        LocalDateTime rangeStart,

        @JsonFormat(pattern = Formatter.PATTERN)
        LocalDateTime rangeEnd,

        int from,
        int size) {
}