package ru.practicum.ewmService.comment.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewmService.comment.dto.CommentDto;
import ru.practicum.ewmService.comment.dto.NewCommentDto;
import ru.practicum.ewmService.comment.enums.CommentStatus;
import ru.practicum.ewmService.comment.mapper.CommentsMapper;
import ru.practicum.ewmService.comment.model.Comment;
import ru.practicum.ewmService.comment.repository.CommentsRepository;
import ru.practicum.ewmService.event.enums.EventState;
import ru.practicum.ewmService.event.model.Event;
import ru.practicum.ewmService.event.repository.EventRepository;
import ru.practicum.ewmService.exceptions.IntegrityException;
import ru.practicum.ewmService.exceptions.NotFoundException;
import ru.practicum.ewmService.participation.enums.ParticipationStatus;
import ru.practicum.ewmService.participation.repository.ParticipationRepository;
import ru.practicum.ewmService.user.model.User;
import ru.practicum.ewmService.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentsServiceImplTest {

    @Mock
    private CommentsRepository commentsRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ParticipationRepository participationRepository;

    @InjectMocks
    private CommentsServiceImpl commentsService;

    @Test
    void addComment_UserNotFound_ThrowsNotFoundException() {
        long userId = 1L;
        long eventId = 1L;
        NewCommentDto newCommentDto = new NewCommentDto("Test comment", null);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentsService.addComment(userId, eventId, newCommentDto));
    }

    @Test
    void addComment_EventNotFound_ThrowsNotFoundException() {
        long userId = 1L;
        long eventId = 1L;
        NewCommentDto newCommentDto = new NewCommentDto("Test comment", null);
        User user = new User();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentsService.addComment(userId, eventId, newCommentDto));
    }

    @Test
    void addComment_EventNotPublished_ThrowsIntegrityException() {
        long userId = 1L;
        long eventId = 1L;
        NewCommentDto newCommentDto = new NewCommentDto("Test comment", null);
        User user = new User();
        Event event = new Event();
        event.setState(EventState.PENDING);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        assertThrows(IntegrityException.class, () -> commentsService.addComment(userId, eventId, newCommentDto));
    }

    @Test
    void addComment_CommentAlreadyExists_ThrowsIntegrityException() {
        long userId = 1L;
        long eventId = 1L;
        NewCommentDto newCommentDto = new NewCommentDto("Test comment", null);
        User user = new User();
        Event event = new Event();
        event.setState(EventState.PUBLISHED);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(commentsRepository.existsByEventAndCommenter(event, user)).thenReturn(true);

        assertThrows(IntegrityException.class, () -> commentsService.addComment(userId, eventId, newCommentDto));
    }

    @Test
    void addComment_RateNotNullButNoParticipation_ThrowsIntegrityException() {
        long userId = 1L;
        long eventId = 1L;
        NewCommentDto newCommentDto = new NewCommentDto("Test comment", 5);
        User user = new User();
        Event event = new Event();
        event.setState(EventState.PUBLISHED);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(commentsRepository.existsByEventAndCommenter(event, user)).thenReturn(false);
        when(participationRepository.existsByEventAndRequesterAndStatus(event, user, ParticipationStatus.CONFIRMED))
                .thenReturn(false);

        assertThrows(IntegrityException.class, () -> commentsService.addComment(userId, eventId, newCommentDto));
    }

    @Test
    void addComment_ValidInput_ReturnsCommentDto() {
        long userId = 1L;
        long eventId = 1L;
        NewCommentDto newCommentDto = new NewCommentDto("Test comment", 5);
        User user = new User();
        Event event = new Event();
        event.setState(EventState.PUBLISHED);
        event.setId(1L); // Добавляем ID события
        Comment comment = new Comment();
        comment.setId(1L);
        CommentDto commentDto = new CommentDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(commentsRepository.existsByEventAndCommenter(event, user)).thenReturn(false);
        when(participationRepository.existsByEventAndRequesterAndStatus(event, user, ParticipationStatus.CONFIRMED))
                .thenReturn(true);
        when(commentsRepository.save(any(Comment.class))).thenReturn(comment);

        try (MockedStatic<CommentsMapper> mockedMapper = mockStatic(CommentsMapper.class)) {
            mockedMapper.when(() -> CommentsMapper.toComment(newCommentDto)).thenReturn(comment);
            mockedMapper.when(() -> CommentsMapper.toDto(comment)).thenReturn(commentDto);

            CommentDto result = commentsService.addComment(userId, eventId, newCommentDto);

            assertNotNull(result);
            verify(commentsRepository).save(comment);
        }
    }

    @Test
    void updateCommentStatus_InvalidStatus_ThrowsIntegrityException() {
        long commentId = 1L;
        CommentStatus status = CommentStatus.PENDING;

        assertThrows(IntegrityException.class, () -> commentsService.updateCommentStatus(commentId, status));
    }

    @Test
    void updateCommentStatus_CommentNotFound_ThrowsNotFoundException() {
        long commentId = 1L;
        CommentStatus status = CommentStatus.APPROVED;

        when(commentsRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentsService.updateCommentStatus(commentId, status));
    }

    @Test
    void updateCommentStatus_CommentNotPending_ThrowsIntegrityException() {
        long commentId = 1L;
        CommentStatus status = CommentStatus.APPROVED;
        Comment comment = new Comment();
        comment.setStatus(CommentStatus.APPROVED);

        when(commentsRepository.findById(commentId)).thenReturn(Optional.of(comment));

        assertThrows(IntegrityException.class, () -> commentsService.updateCommentStatus(commentId, status));
    }

    @Test
    void updateCommentStatus_ValidInput_ReturnsCommentDto() {
        long commentId = 1L;
        CommentStatus status = CommentStatus.APPROVED;
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setStatus(CommentStatus.PENDING);
        comment.setRate(5);
        Event event = new Event();
        event.setId(1L);
        comment.setEvent(event);
        CommentDto commentDto = new CommentDto();

        when(commentsRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentsRepository.save(comment)).thenReturn(comment);
        when(commentsRepository.getAverageRate(event)).thenReturn(5.0);

        try (MockedStatic<CommentsMapper> mockedMapper = mockStatic(CommentsMapper.class)) {
            mockedMapper.when(() -> CommentsMapper.toDto(comment)).thenReturn(commentDto);

            CommentDto result = commentsService.updateCommentStatus(commentId, status);

            assertNotNull(result);
            assertEquals(CommentStatus.APPROVED, comment.getStatus());
            verify(commentsRepository).save(comment);
            verify(eventRepository).save(event);
        }
    }
}