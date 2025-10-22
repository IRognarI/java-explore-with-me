package ru.practicum.ewmService.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmService.event.dto.EventShortDto;

/**
 * <p>DTO для получения информации о комментариях заданного <u>пользователя</u>.</p>
 * <p>Поскольку id пользователя задается в запросе, возвращать подробную информацию о нем
 * в каждом комментарии бессмысленно. Для сокращения нагрузки на БД здесь выдается только
 * подробная информация о событии комментария.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentWithEventDetailedDto {

    private long id;
    private EventShortDto event;
    private long commenter;
    private String text;
    private Integer rate;
    private String status;
    private String created;
}