package ru.practicum.ewmService.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmService.event.dto.EventShortDto;
import ru.practicum.ewmService.user.dto.UserShortDto;

/**
 * Data Transfer Object (DTO) для подробной информации о комментарии.
 * Этот класс инкапсулирует детали комментария, включая его ID, связанное событие,
 * информацию о комментаторе, текст, рейтинг, статус и временную метку создания.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDetailedDto {

    private long id;
    private EventShortDto event;
    private UserShortDto commenter;
    private String text;
    private Integer rate;
    private String status;
    private String created;
}