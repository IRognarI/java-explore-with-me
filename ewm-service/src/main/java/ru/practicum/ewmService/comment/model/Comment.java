package ru.practicum.ewmService.comment.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewmService.comment.enums.CommentStatus;
import ru.practicum.ewmService.dateTimeFormatter.Formatter;
import ru.practicum.ewmService.event.model.Event;
import ru.practicum.ewmService.user.model.User;

import java.time.LocalDateTime;
import java.util.Objects;


/**
 * Комментарий к событию.<br>
 * <li>Комментарии могут оставлять только зарегистрированные пользователи.</li>
 * <li>Комментировать можно только опубликованные события, иначе - ошибка 409</li>
 * <li>Повторные комментарии недопустимы - ошибка 409</li>
 * <li>Рейтинг комментария допустим только для пользователей с подтвержденным участием - иначе ошибка 409.</li>
 * <li>Статус нового комментария - PENDING </li>
 * <li>Просматривать комментарии могут все пользователи</li>
 * <li>Пользователь может изменить свой комментарий, после чего его необходимо модерировать заново.
 * Статус комментария после его изменения - PENDING.</li>
 * <li>Попытка изменить чужой комментарий приводит к ошибке 409. </li>
 * <li>Модерация всех комментариев осуществляется администратором.
 * <p>Статус модерации:<br>
 * Admin: PENDING -> APPROVED || PENDING -> REJECTED;<br>
 * User:  APPROVED -> PENDING || REJECTED -> PENDING<br>
 * Пересчет рейтинга события осуществляется при смене статуса<br>
 * PENDING -> APPROVED и APPROVED -> PENDING </p></li>
 * <li>Удаление комментария автором ограничивается состоянием PENDING</li>
 * <li>Администратор может удалить комментарий в любом состоянии</li>
 * <p>
 * Публичный API дополнен возможностью получения списка событий с сортировкой по уменьшению рейтинга.
 */


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "commenter_id")
    private User commenter;

    private String text;

    @Enumerated(EnumType.STRING)
    private CommentStatus status;
    private Integer rate;

    @JsonFormat(pattern = Formatter.PATTERN)
    private LocalDateTime created;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Comment that = (Comment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}