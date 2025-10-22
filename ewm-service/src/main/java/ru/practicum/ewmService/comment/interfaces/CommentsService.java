package ru.practicum.ewmService.comment.interfaces;

import jakarta.validation.Valid;
import ru.practicum.ewmService.comment.dto.CommentDetailedDto;
import ru.practicum.ewmService.comment.dto.CommentDto;
import ru.practicum.ewmService.comment.dto.CommentWithEventDetailedDto;
import ru.practicum.ewmService.comment.dto.CommentWithUserDetailedDto;
import ru.practicum.ewmService.comment.dto.NewCommentDto;
import ru.practicum.ewmService.comment.dto.UpdateCommentDto;
import ru.practicum.ewmService.comment.enums.CommentStatus;

import java.util.List;

public interface CommentsService {

    CommentDto addComment(long userId, long eventId, @Valid NewCommentDto newCommentDto);

    CommentDto updateComment(long userId, long commentId, @Valid UpdateCommentDto updateCommentDto);

    CommentDto updateCommentStatus(long commentId, CommentStatus status);

    List<CommentWithUserDetailedDto> getCommentsByEvent(long eventId, Boolean rated, int from, int size);

    List<CommentWithEventDetailedDto> getCommentsByUser(long userId, Boolean rated, int from, int size);

    void deleteComment(long userId, long commentId);

    void deleteComment(long commentId);

    CommentDetailedDto getComment(long commentId);
}