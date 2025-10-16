package ru.practicum.ewmService.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmService.category.dto.CategoryDto;
import ru.practicum.ewmService.user.dto.UserShortDto;

/**
 * Объект передачи данных (DTO) для представления краткой версии события.
 * Этот класс включает основную информацию о событии, такую как его ID, аннотация,
 * категория, количество подтвержденных запросов, дата события, инициатор, статус оплаты, заголовок и количество просмотров.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {

    private Long id;
    private String annotation;
    private CategoryDto category;
    private long confirmedRequests;
    private String eventDate;
    private UserShortDto initiator;
    private boolean paid;
    private String title;
    private long views;
}