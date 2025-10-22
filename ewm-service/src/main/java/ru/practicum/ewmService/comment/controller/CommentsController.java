package ru.practicum.ewmService.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewmService.comment.dto.CommentDetailedDto;
import ru.practicum.ewmService.comment.dto.CommentDto;
import ru.practicum.ewmService.comment.dto.CommentWithEventDetailedDto;
import ru.practicum.ewmService.comment.dto.CommentWithUserDetailedDto;
import ru.practicum.ewmService.comment.dto.NewCommentDto;
import ru.practicum.ewmService.comment.dto.UpdateCommentDto;
import ru.practicum.ewmService.comment.enums.CommentStatus;
import ru.practicum.ewmService.comment.interfaces.CommentsService;

import java.util.List;

/**
 * <h1>Контроллер управления комментариями</h1>
 *
 * <p>Предоставляет REST API для работы с комментариями к событиям, включая:</p>
 * <ul>
 *   <li>Модерацию комментариев (администратор)</li>
 *   <li>Получение информации о комментариях (публичный доступ)</li>
 *   <li>Добавление, обновление и удаление комментариев (приватный доступ для пользователей)</li>
 *   <li>Управление комментариями администратором</li>
 * </ul>
 *
 * <h2>Типы пользователей и доступ</h2>
 * <ul>
 *   <li><strong>Администратор</strong> - имеет доступ к модерации и удалению любых комментариев</li>
 *   <li><strong>Пользователь</strong> - может добавлять, редактировать и удалять свои комментарии</li>
 *   <li><strong>Публичный доступ</strong> - возможность просмотра опубликованных комментариев</li>
 * </ul>
 *
 * <h2>Функциональность</h2>
 *
 * <h3>Модерация комментариев (Admin API)</h3>
 * <p>Администратор может устанавливать статус комментария (APPROVED/REJECTED) и удалять любые комментарии.
 * При изменении статуса комментария на APPROVED пересчитывается рейтинг события.</p>
 *
 * <h3>Получение комментариев (Public API)</h3>
 * <p>Доступно без авторизации. Позволяет получать:
 * <ul>
 *   <li>Информацию о конкретном комментарии по его ID</li>
 *   <li>Список комментариев к событию с пагинацией</li>
 *   <li>Список комментариев пользователя с пагинацией</li>
 * </ul>
 * </p>
 *
 * <h3>Управление комментариями (Private API)</h3>
 * <p>Доступно только авторизованным пользователям для работы со своими комментариями:
 * <ul>
 *   <li>Добавление нового комментария к опубликованному событию</li>
 *   <li>Обновление текста и рейтинга собственного комментария</li>
 *   <li>Удаление собственного комментария (только в статусе PENDING)</li>
 * </ul>
 * </p>
 *
 * <h2>Особенности работы с рейтингами</h2>
 * <ul>
 *   <li>Рейтинг события рассчитывается как среднее арифметическое рейтингов всех APPROVED комментариев</li>
 *   <li>Пользователь может указать рейтинг только если у него есть подтвержденная заявка на участие в событии</li>
 *   <li>При изменении статуса комментария или его рейтинга автоматически пересчитывается рейтинг события</li>
 * </ul>
 *
 * <h2>Статусы комментариев</h2>
 * <ul>
 *   <li><strong>PENDING</strong> - комментарий ожидает модерации (начальный статус после создания/обновления)</li>
 *   <li><strong>APPROVED</strong> - комментарий одобрен и учитывается при расчете рейтинга</li>
 *   <li><strong>REJECTED</strong> - комментарий отклонен и не учитывается при расчете рейтинга</li>
 * </ul>
 *
 * <h2>Валидация данных</h2>
 * <ul>
 *   <li>Все входные данные проходят валидацию через аннотации Jakarta Validation</li>
 *   <li>Проверяется существование пользователей, событий и комментариев</li>
 *   <li>Проверяется корректность статусов и прав доступа</li>
 * </ul>
 *
 * <h2>Пагинация</h2>
 * <p>Для списков комментариев реализована пагинация с параметрами:
 * <ul>
 *   <li><strong>from</strong> - начальный индекс (по умолчанию 0)</li>
 *   <li><strong>size</strong> - размер страницы (по умолчанию 10)</li>
 * </ul>
 * </p>
 *
 * @see CommentsService
 * @see CommentDto
 * @see NewCommentDto
 * @see UpdateCommentDto
 * @see CommentStatus
 */
@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping
public class CommentsController {

    private final CommentsService commentsService;

    @PatchMapping("/admin/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateCommentStatus(@PathVariable long commentId,
                                          @RequestParam CommentStatus status) {

        log.info("Update comment id={} with status {} admin PATCH request", commentId, status);
        return commentsService.updateCommentStatus(commentId, status);
    }

    @GetMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDetailedDto getComment(@PathVariable long commentId) {

        log.info("Get comment id={} public GET request", commentId);
        return commentsService.getComment(commentId);
    }

    @GetMapping("/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentWithUserDetailedDto> getCommentsByEvent(
            @PathVariable long eventId,
            @RequestParam(required = false) Boolean rated,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size) {

        log.info("Get comments of event id={} public GET request", eventId);
        return commentsService.getCommentsByEvent(eventId, rated, from, size);
    }

    @GetMapping("/users/{userId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentWithEventDetailedDto> getCommentsByUser(
            @PathVariable long userId,
            @RequestParam(required = false) Boolean rated,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size) {

        log.info("Get comments of user id={} public GET request", userId);
        return commentsService.getCommentsByUser(userId, rated, from, size);
    }

    @PostMapping("/users/{userId}/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable long userId,
                                 @PathVariable long eventId,
                                 @Valid @RequestBody NewCommentDto newCommentDto) {

        log.info("Add comment private POST request: userId={}, eventId={}", userId, eventId);
        return commentsService.addComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(@PathVariable long userId,
                                    @PathVariable long commentId,
                                    @Valid @RequestBody UpdateCommentDto updateCommentDto) {

        log.info("Update comment private POST request: userId={}, commentId={}", userId, commentId);
        return commentsService.updateComment(userId, commentId, updateCommentDto);
    }

    @DeleteMapping("/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long userId, @PathVariable long commentId) {

        log.info("Delete comment private DELETE request: userId={}, commentId={}", userId, commentId);
        commentsService.deleteComment(userId, commentId);
    }

    @DeleteMapping("/admin/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long commentId) {

        log.info("Delete comment admin DELETE request: commentId={}", commentId);
        commentsService.deleteComment(commentId);
    }
}