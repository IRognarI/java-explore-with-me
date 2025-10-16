package ru.practicum.ewmService.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmService.category.dto.CategoryDto;
import ru.practicum.ewmService.event.location.Location;
import ru.practicum.ewmService.user.dto.UserShortDto;

/**
 * Объект передачи данных (DTO) представляющий полную информацию о событии.
 * Используется для передачи полных данных о событии между слоями приложения.
 * Включает все поля события такие как: id, аннотация, категория, подтвержденные запросы,
 * дата создания, описание, дата события, инициатор, местоположение, детали оплаты,
 * лимит участников, дата публикации, флаг модерации запросов, состояние, заголовок и количество просмотров.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {
    private Long id;
    private String annotation;

    private CategoryDto category;

    private long confirmedRequests;
    private String createdOn;
    private String description;
    private String eventDate;
    private UserShortDto initiator;
    private Location location;
    private boolean paid;
    private int participantLimit = 0;
    private String publishedOn;
    private boolean requestModeration = true;
    private String state;
    private String title;
    private long views = 0L;
}