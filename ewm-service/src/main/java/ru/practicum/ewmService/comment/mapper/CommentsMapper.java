package ru.practicum.ewmService.comment.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmService.comment.dto.CommentDetailedDto;
import ru.practicum.ewmService.comment.dto.CommentDto;
import ru.practicum.ewmService.comment.dto.CommentWithEventDetailedDto;
import ru.practicum.ewmService.comment.dto.CommentWithUserDetailedDto;
import ru.practicum.ewmService.comment.dto.NewCommentDto;
import ru.practicum.ewmService.comment.enums.CommentStatus;
import ru.practicum.ewmService.comment.model.Comment;
import ru.practicum.ewmService.dateTimeFormatter.Formatter;
import ru.practicum.ewmService.event.dto.EventShortDto;
import ru.practicum.ewmService.user.dto.UserShortDto;

import java.time.LocalDateTime;

@UtilityClass
public class CommentsMapper {

    public Comment toComment(NewCommentDto newCommentDto) {

        var comment = new Comment();
        comment.setText(newCommentDto.getText());
        comment.setRate(newCommentDto.getRate());
        comment.setStatus(CommentStatus.PENDING);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public CommentDto toDto(Comment comment) {

        return new CommentDto(
                comment.getId(),
                comment.getEvent().getId(),
                comment.getCommenter().getId(),
                comment.getText(),
                comment.getRate(),
                comment.getStatus().name(),
                comment.getCreated().format(Formatter.FORMATTER)
        );
    }

    public CommentWithEventDetailedDto toCommentOfEventDto(Comment comment, EventShortDto eventShortDto) {

        return new CommentWithEventDetailedDto(
                comment.getId(),
                eventShortDto,
                comment.getCommenter().getId(),
                comment.getText(),
                comment.getRate(),
                comment.getStatus().name(),
                comment.getCreated().format(Formatter.FORMATTER)
        );
    }

    public CommentWithUserDetailedDto toCommentOfUserDto(Comment comment, UserShortDto userShortDto) {

        return new CommentWithUserDetailedDto(
                comment.getId(),
                comment.getEvent().getId(),
                userShortDto,
                comment.getText(),
                comment.getRate(),
                comment.getStatus().name(),
                comment.getCreated().format(Formatter.FORMATTER)
        );
    }

    public CommentDetailedDto toCommentDetailedDto(Comment comment,
                                                   UserShortDto userShortDto,
                                                   EventShortDto eventShortDto) {

        return new CommentDetailedDto(
                comment.getId(),
                eventShortDto,
                userShortDto,
                comment.getText(),
                comment.getRate(),
                comment.getStatus().name(),
                comment.getCreated().format(Formatter.FORMATTER)
        );
    }
}