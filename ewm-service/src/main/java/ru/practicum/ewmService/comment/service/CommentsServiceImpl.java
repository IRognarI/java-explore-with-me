package ru.practicum.ewmService.comment.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewmService.comment.dto.CommentDetailedDto;
import ru.practicum.ewmService.comment.dto.CommentDto;
import ru.practicum.ewmService.comment.dto.CommentWithEventDetailedDto;
import ru.practicum.ewmService.comment.dto.CommentWithUserDetailedDto;
import ru.practicum.ewmService.comment.dto.NewCommentDto;
import ru.practicum.ewmService.comment.dto.UpdateCommentDto;
import ru.practicum.ewmService.comment.enums.CommentStatus;
import ru.practicum.ewmService.comment.interfaces.CommentsService;
import ru.practicum.ewmService.comment.mapper.CommentsMapper;
import ru.practicum.ewmService.comment.model.Comment;
import ru.practicum.ewmService.comment.repository.CommentsRepository;
import ru.practicum.ewmService.event.enums.EventState;
import ru.practicum.ewmService.event.mapper.EventMapper;
import ru.practicum.ewmService.event.model.Event;
import ru.practicum.ewmService.event.repository.EventRepository;
import ru.practicum.ewmService.exceptions.IntegrityException;
import ru.practicum.ewmService.exceptions.NotFoundException;
import ru.practicum.ewmService.participation.enums.ParticipationStatus;
import ru.practicum.ewmService.participation.repository.ParticipationRepository;
import ru.practicum.ewmService.user.mapper.UserMapper;
import ru.practicum.ewmService.user.model.User;
import ru.practicum.ewmService.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

/**
 * Реализация сервиса для управления комментариями.
 *
 * <p>Сервис предоставляет функциональность для:</p>
 * <ul>
 *   <li>Добавления новых комментариев к событиям</li>
 *   <li>Обновления существующих комментариев</li>
 *   <li>Модерации комментариев (одобрение/отклонение)</li>
 *   <li>Получения информации о комментариях по событиям и пользователям</li>
 *   <li>Удаления комментариев</li>
 * </ul>
 *
 * <p>Основные бизнес-правила:</p>
 * <ul>
 *   <li>Комментировать можно только опубликованные события</li>
 *   <li>Один пользователь может оставить только один комментарий к событию</li>
 *   <li>Рейтинг события может выставлять только пользователь, подтвердивший участие</li>
 *   <li>После обновления комментарий требует модерации</li>
 *   <li>Изменение рейтинга обновляет средний рейтинг события</li>
 * </ul>
 *
 * <p>Сервис использует следующие репозитории:</p>
 * <ul>
 *   <li>{@link CommentsRepository} - для работы с комментариями</li>
 *   <li>{@link UserRepository} - для проверки существования пользователей</li>
 *   <li>{@link EventRepository} - для проверки существования событий</li>
 *   <li>{@link ParticipationRepository} - для проверки подтвержденного участия</li>
 * </ul>
 *
 * @see CommentsService
 * @see Comment
 * @see CommentDto
 * @see UpdateCommentDto
 * @see NewCommentDto
 */
@Slf4j
@Service
@AllArgsConstructor
public class CommentsServiceImpl implements CommentsService {

    private final CommentsRepository commentsRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ParticipationRepository participationRepository;

    @Override
    public CommentDto addComment(long userId, long eventId, NewCommentDto newCommentDto) {
        User user = checkUser(userId);

        Event event = checkEvent(eventId);
        if (event.getState() != EventState.PUBLISHED) {
            throw new IntegrityException("Event id=%d is not published yet".formatted(eventId));
        }

        if (commentsRepository.existsByEventAndCommenter(event, user)) {
            throw new IntegrityException("Comment from user id=%d to event id=%d already exists"
                    .formatted(userId, eventId));
        }

        if (newCommentDto.getRate() != null) {
            checkUseRate(event, user);
        }

        Comment comment = CommentsMapper.toComment(newCommentDto);
        comment.setEvent(event);
        comment.setCommenter(user);
        comment = commentsRepository.save(comment);

        log.info("Comment with id={} from user id={} to event id={} has been saved", comment.getId(), userId, eventId);
        return CommentsMapper.toDto(comment);
    }

    @Override
    public CommentDto updateComment(long userId, long commentId, UpdateCommentDto updateCommentDto) {
        User user = checkUser(userId);

        Comment comment = checkComment(commentId);

        if (comment.getCommenter() != user) {
            throw new IntegrityException("Comment with id=%d is not owned by commenter with id=%d"
                    .formatted(commentId, userId));
        }

        if (updateCommentDto.getText() != null) {
            comment.setText(updateCommentDto.getText());
        }
        Integer oldRate = comment.getRate();
        if (updateCommentDto.getRate() != null) {

            checkUseRate(comment.getEvent(), user);
            comment.setRate(updateCommentDto.getRate());
        }
        CommentStatus oldStatus = comment.getStatus();
        comment.setStatus(CommentStatus.PENDING);
        comment = commentsRepository.save(comment);

        if (!Objects.equals(oldRate, comment.getRate()) && oldStatus == CommentStatus.APPROVED) {
            updateEventRate(comment.getEvent());
        }

        log.info("Comment with id={} from user id={} to event id={} has been updated",
                comment.getId(), userId, comment.getEvent().getId());
        return CommentsMapper.toDto(comment);
    }

    @Override
    public CommentDto updateCommentStatus(long commentId, CommentStatus status) {

        if (status != CommentStatus.APPROVED && status != CommentStatus.REJECTED) {
            throw new IntegrityException("Comment status for comment moderation API must be APPROVED or REJECTED");
        }

        Comment comment = checkComment(commentId);

        if (comment.getStatus() != CommentStatus.PENDING) {
            throw new IntegrityException("Comment with id=%d must has status PENDING for moderation");
        }

        CommentStatus oldStatus = comment.getStatus();
        comment.setStatus(status);
        comment = commentsRepository.save(comment);

        if (comment.getRate() != null) {
            if (status == CommentStatus.APPROVED) {
                updateEventRate(comment.getEvent());
            }
        }

        log.info("Status of comment with id={} has been updated to {}", commentId, status);
        return CommentsMapper.toDto(comment);
    }

    @Override
    public CommentDetailedDto getComment(long commentId) {

        Comment comment = checkComment(commentId);
        var userDto = UserMapper.toUserShortDto(comment.getCommenter());
        var eventDto = EventMapper.toShortDto(comment.getEvent());
        var commentDto = CommentsMapper.toCommentDetailedDto(comment, userDto, eventDto);
        log.info("Get comment request returns comment dto with id={}", commentId);
        return commentDto;
    }

    @Override
    public List<CommentWithUserDetailedDto> getCommentsByEvent(long eventId, Boolean rated, int from, int size) {

        Event event = checkEvent(eventId);
        Page<Comment> page = (rated != null) ?
                commentsRepository.findAllByEventAndRateIsNotNullOrderByCreatedAsc(event,
                        PageRequest.of(from, size)) :
                commentsRepository.findAllByEventOrderByCreatedAsc(event,
                        PageRequest.of(from, size));
        List<CommentWithUserDetailedDto> dtos = page.getContent().stream()
                .map(c -> CommentsMapper.toCommentOfUserDto(
                        c, UserMapper.toUserShortDto(c.getCommenter())))
                .toList();
        log.info("Get comments by event id={} returns {} records", eventId, dtos.size());
        return dtos;
    }

    @Override
    public List<CommentWithEventDetailedDto> getCommentsByUser(long userId, Boolean rated, int from, int size) {

        User user = checkUser(userId);
        Page<Comment> page = (rated != null) ?
                commentsRepository.findAllByCommenterAndRateIsNotNullOrderByCreatedAsc(user,
                        PageRequest.of(from, size)) :
                commentsRepository.findAllByCommenterOrderByCreatedAsc(user,
                        PageRequest.of(from, size));
        List<CommentWithEventDetailedDto> dtos = page.getContent().stream()
                .map(c -> CommentsMapper.toCommentOfEventDto(
                        c, EventMapper.toShortDto(c.getEvent())))
                .toList();
        log.info("Get comments by user id={} returns {} records", userId, dtos.size());
        return dtos;
    }

    @Override
    public void deleteComment(long userId, long commentId) {

        User user = checkUser(userId);
        Comment comment = checkComment(commentId);
        if (comment.getStatus() != CommentStatus.PENDING) {
            throw new IntegrityException("Comment with id=%d must has status PENDING for deletion");
        }
        commentsRepository.delete(comment);
        log.info("Comment with id={} by user id={} has been deleted", commentId, userId);
    }

    @Override
    public void deleteComment(long commentId) {

        Comment comment = commentsRepository.findById(commentId).orElse(null);
        if (comment != null) {
            commentsRepository.delete(comment);
            if (comment.getRate() != null && comment.getStatus() == CommentStatus.APPROVED) {
                updateEventRate(comment.getEvent());
            }
            log.info("Comment with id={} has been deleted", commentId);
        }
    }

    private User checkUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("User with id=%d not found".formatted(userId)));
    }

    private Comment checkComment(long commentId) {
        return commentsRepository.findById(commentId)
                .orElseThrow(() ->
                        new NotFoundException("Comment with id=%d not found".formatted(commentId)));
    }

    private Event checkEvent(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new NotFoundException("Event with id=%d not found".formatted(eventId)));
    }

    private void checkUseRate(Event event, User user) {

        if (!participationRepository.existsByEventAndRequesterAndStatus(event,
                user,
                ParticipationStatus.CONFIRMED)) {
            throw new IntegrityException("Event rating needs confirmed participation request for user id=%d"
                    .formatted(user.getId()));
        }
    }

    private void updateEventRate(Event event) {

        var avgRate = commentsRepository.getAverageRate(event);
        event.setRating(avgRate == null ? 0 : avgRate);
        eventRepository.save(event);
        log.info("Event with id={} updated rate={}", event.getId(), avgRate);
    }
}