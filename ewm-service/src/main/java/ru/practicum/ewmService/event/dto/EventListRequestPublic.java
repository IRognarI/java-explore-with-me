package ru.practicum.ewmService.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.ewmService.dateTimeFormatter.Formatter;
import ru.practicum.ewmService.event.enums.SortingMode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Класс-запись, представляющий публичный запрос на список событий.
 * Этот класс инкапсулирует различные критерии фильтрации и сортировки для поиска событий.
 *
 * @param text          текст для поиска в аннотациях и описаниях событий
 * @param categories    список идентификаторов категорий для фильтрации событий
 * @param paid          фильтр по статусу оплаты событий (true для платных событий, false для бесплатных событий, null для отсутствия фильтра)
 * @param rangeStart    начальная дата и время для фильтра диапазона дат событий
 * @param rangeEnd      конечная дата и время для фильтра диапазона дат событий
 * @param onlyAvailable фильтр только доступных событий (true для включения только событий с доступными местами для участников)
 * @param sortingMode   режим сортировки для применения к списку событий
 * @param from          начальный индекс для пагинации
 * @param size          количество событий для возврата при пагинации
 */
public record EventListRequestPublic(

        String text,
        List<Long> categories,
        Boolean paid,

        @JsonFormat(pattern = Formatter.PATTERN)
        LocalDateTime rangeStart,

        @JsonFormat(pattern = Formatter.PATTERN)
        LocalDateTime rangeEnd,
        Boolean onlyAvailable,
        SortingMode sortingMode,
        int from,
        int size) {

}