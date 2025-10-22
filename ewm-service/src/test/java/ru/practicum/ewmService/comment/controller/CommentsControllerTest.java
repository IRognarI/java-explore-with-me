package ru.practicum.ewmService.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewmService.comment.dto.CommentDetailedDto;
import ru.practicum.ewmService.comment.dto.CommentDto;
import ru.practicum.ewmService.comment.dto.CommentWithEventDetailedDto;
import ru.practicum.ewmService.comment.dto.CommentWithUserDetailedDto;
import ru.practicum.ewmService.comment.dto.NewCommentDto;
import ru.practicum.ewmService.comment.dto.UpdateCommentDto;
import ru.practicum.ewmService.comment.enums.CommentStatus;
import ru.practicum.ewmService.comment.interfaces.CommentsService;
import ru.practicum.ewmService.event.dto.EventShortDto;
import ru.practicum.ewmService.user.dto.UserShortDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentsController.class)
class CommentsControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CommentsService commentsService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void updateCommentStatus() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Updated comment");
        commentDto.setEvent(1L);
        commentDto.setCommenter(1L);
        commentDto.setRate(5);
        commentDto.setStatus("APPROVED");
        commentDto.setCreated("2023-01-01 12:00:00");

        when(commentsService.updateCommentStatus(anyLong(), any(CommentStatus.class))).thenReturn(commentDto);

        mvc.perform(patch("/admin/comments/1?status=APPROVED")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("Updated comment")))
                .andExpect(jsonPath("$.event", is(1)))
                .andExpect(jsonPath("$.commenter", is(1)))
                .andExpect(jsonPath("$.rate", is(5)))
                .andExpect(jsonPath("$.status", is("APPROVED")))
                .andExpect(jsonPath("$.created", is("2023-01-01 12:00:00")));
    }

    @Test
    void getComment() throws Exception {
        UserShortDto userShortDto = new UserShortDto(1L, "User Name");
        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setId(1L);

        CommentDetailedDto commentDetailedDto = new CommentDetailedDto();
        commentDetailedDto.setId(1L);
        commentDetailedDto.setText("Comment text");
        commentDetailedDto.setCommenter(userShortDto);
        commentDetailedDto.setEvent(eventShortDto);
        commentDetailedDto.setRate(5);
        commentDetailedDto.setStatus("APPROVED");
        commentDetailedDto.setCreated("2023-01-01 12:00:00");

        when(commentsService.getComment(anyLong())).thenReturn(commentDetailedDto);

        mvc.perform(get("/comments/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("Comment text")))
                .andExpect(jsonPath("$.commenter.id", is(1)))
                .andExpect(jsonPath("$.commenter.name", is("User Name")))
                .andExpect(jsonPath("$.event.id", is(1)))
                .andExpect(jsonPath("$.rate", is(5)))
                .andExpect(jsonPath("$.status", is("APPROVED")))
                .andExpect(jsonPath("$.created", is("2023-01-01 12:00:00")));
    }

    @Test
    void getCommentsByEvent() throws Exception {
        UserShortDto userShortDto = new UserShortDto();
        userShortDto.setId(1L);
        userShortDto.setName("User Name");

        CommentWithUserDetailedDto commentWithUserDetailedDto = new CommentWithUserDetailedDto();
        commentWithUserDetailedDto.setId(1L);
        commentWithUserDetailedDto.setText("Event comment");
        commentWithUserDetailedDto.setCommenter(userShortDto);
        commentWithUserDetailedDto.setEvent(1L);
        commentWithUserDetailedDto.setRate(5);
        commentWithUserDetailedDto.setStatus("APPROVED");
        commentWithUserDetailedDto.setCreated("2023-01-01 12:00:00");

        when(commentsService.getCommentsByEvent(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(commentWithUserDetailedDto));

        mvc.perform(get("/events/1/comments")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].text", is("Event comment")))
                .andExpect(jsonPath("$[0].commenter.id", is(1)))
                .andExpect(jsonPath("$[0].commenter.name", is("User Name")))
                .andExpect(jsonPath("$[0].event", is(1)))
                .andExpect(jsonPath("$[0].rate", is(5)))
                .andExpect(jsonPath("$[0].status", is("APPROVED")))
                .andExpect(jsonPath("$[0].created", is("2023-01-01 12:00:00")));
    }

    @Test
    void getCommentsByUser() throws Exception {
        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setId(1L);
        eventShortDto.setAnnotation("Event annotation");
        eventShortDto.setConfirmedRequests(0L);
        eventShortDto.setEventDate("2023-01-01 12:00:00");
        eventShortDto.setPaid(false);
        eventShortDto.setTitle("Event title");
        eventShortDto.setViews(0L);
        eventShortDto.setRating(0.0);

        CommentWithEventDetailedDto commentWithEventDetailedDto = new CommentWithEventDetailedDto();
        commentWithEventDetailedDto.setId(1L);
        commentWithEventDetailedDto.setText("User comment");
        commentWithEventDetailedDto.setEvent(eventShortDto);
        commentWithEventDetailedDto.setCommenter(1L);
        commentWithEventDetailedDto.setRate(5);
        commentWithEventDetailedDto.setStatus("APPROVED");
        commentWithEventDetailedDto.setCreated("2023-01-01 12:00:00");

        when(commentsService.getCommentsByUser(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(commentWithEventDetailedDto));

        mvc.perform(get("/users/1/comments")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].text", is("User comment")))
                .andExpect(jsonPath("$[0].event.id", is(1)))
                .andExpect(jsonPath("$[0].commenter", is(1)))
                .andExpect(jsonPath("$[0].rate", is(5)))
                .andExpect(jsonPath("$[0].status", is("APPROVED")))
                .andExpect(jsonPath("$[0].created", is("2023-01-01 12:00:00")));
    }

    @Test
    void addComment() throws Exception {
        NewCommentDto newCommentDto = new NewCommentDto();
        newCommentDto.setText("New comment");

        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("New comment");
        commentDto.setEvent(1L);
        commentDto.setCommenter(1L);
        commentDto.setRate(null);
        commentDto.setStatus("PENDING");
        commentDto.setCreated("2023-01-01 12:00:00");

        when(commentsService.addComment(anyLong(), anyLong(), any(NewCommentDto.class))).thenReturn(commentDto);

        mvc.perform(post("/users/1/events/1/comments")
                        .content(mapper.writeValueAsString(newCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("New comment")))
                .andExpect(jsonPath("$.event", is(1)))
                .andExpect(jsonPath("$.commenter", is(1)))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.created", is("2023-01-01 12:00:00")));
    }

    @Test
    void updateComment() throws Exception {
        UpdateCommentDto updateCommentDto = new UpdateCommentDto();
        updateCommentDto.setText("Updated comment");

        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Updated comment");
        commentDto.setEvent(1L);
        commentDto.setCommenter(1L);
        commentDto.setRate(5);
        commentDto.setStatus("APPROVED");
        commentDto.setCreated("2023-01-01 12:00:00");

        when(commentsService.updateComment(anyLong(), anyLong(), any(UpdateCommentDto.class))).thenReturn(commentDto);

        mvc.perform(patch("/users/1/comments/1")
                        .content(mapper.writeValueAsString(updateCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("Updated comment")))
                .andExpect(jsonPath("$.event", is(1)))
                .andExpect(jsonPath("$.commenter", is(1)))
                .andExpect(jsonPath("$.rate", is(5)))
                .andExpect(jsonPath("$.status", is("APPROVED")))
                .andExpect(jsonPath("$.created", is("2023-01-01 12:00:00")));
    }

    @Test
    void deleteCommentByUser() throws Exception {
        doNothing().when(commentsService).deleteComment(anyLong(), anyLong());

        mvc.perform(delete("/users/1/comments/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCommentByAdmin() throws Exception {
        doNothing().when(commentsService).deleteComment(anyLong());

        mvc.perform(delete("/admin/comments/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}