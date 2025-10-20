package ru.practicum.ewmService.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmService.user.dto.UserShortDto;

/**
 * <p>DTO для получения информации о комментариях к заданному <u>событию</u>.</p>
 * <p>Поскольку id события задается в запросе, возвращать подробную информацию о нем
 * в каждом комментарии бессмысленно. Для сокращения нагрузки на БД здесь выдается только
 * подробная информация о пользователе, оставившем комментарий.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentWithUserDetailedDto {

    private long id;
    private long event;
    private UserShortDto commenter;
    private String text;
    private Integer rate;
    private String status;
    private String created;
}