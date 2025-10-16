package ru.practicum.ewmService.participation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewmService.participation.dto.ParticipationRequestDto;
import ru.practicum.ewmService.participation.dto.ParticipationStatusUpdateRequest;
import ru.practicum.ewmService.participation.dto.ParticipationStatusUpdateResult;
import ru.practicum.ewmService.participation.enums.ParticipationStatus;
import ru.practicum.ewmService.participation.interfaces.ParticipationService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ParticipationController.class)
class ParticipationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ParticipationService participationService;

    @Test
    void getUserParticipationRequests() throws Exception {
        long userId = 1L;
        ParticipationRequestDto requestDto = new ParticipationRequestDto();
        requestDto.setId(1L);
        requestDto.setRequester(userId);
        requestDto.setEvent(1L);
        requestDto.setStatus("PENDING");

        List<ParticipationRequestDto> requests = List.of(requestDto);

        when(participationService.getUserRequests(anyLong())).thenReturn(requests);

        mockMvc.perform(get("/users/{userId}/requests", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requests)));

        verify(participationService).getUserRequests(userId);
    }

    @Test
    void addParticipation() throws Exception {
        long userId = 1L;
        long eventId = 1L;

        ParticipationRequestDto requestDto = new ParticipationRequestDto();
        requestDto.setId(1L);
        requestDto.setRequester(userId);
        requestDto.setEvent(eventId);
        requestDto.setStatus("PENDING");

        when(participationService.addRequest(anyLong(), anyLong())).thenReturn(requestDto);

        mockMvc.perform(post("/users/{userId}/requests", userId)
                        .param("eventId", String.valueOf(eventId)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(requestDto)));

        verify(participationService).addRequest(userId, eventId);
    }

    @Test
    void cancelParticipation() throws Exception {
        long userId = 1L;
        long requestId = 1L;

        ParticipationRequestDto requestDto = new ParticipationRequestDto();
        requestDto.setId(requestId);
        requestDto.setRequester(userId);
        requestDto.setEvent(1L);
        requestDto.setStatus("CANCELED");

        when(participationService.cancelRequest(anyLong(), anyLong())).thenReturn(requestDto);

        mockMvc.perform(patch("/users/{userId}/requests/{requestId}/cancel", userId, requestId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requestDto)));

        verify(participationService).cancelRequest(userId, requestId);
    }

    @Test
    void getEventParticipationRequests() throws Exception {
        long userId = 1L;
        long eventId = 1L;

        ParticipationRequestDto requestDto = new ParticipationRequestDto();
        requestDto.setId(1L);
        requestDto.setRequester(2L);
        requestDto.setEvent(eventId);
        requestDto.setStatus("PENDING");

        List<ParticipationRequestDto> requests = List.of(requestDto);

        when(participationService.getEventRequests(anyLong(), anyLong())).thenReturn(requests);

        mockMvc.perform(get("/users/{userId}/events/{eventId}/requests", userId, eventId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requests)));

        verify(participationService).getEventRequests(userId, eventId);
    }

    @Test
    void updateParticipationStatus() throws Exception {
        long userId = 1L;
        long eventId = 1L;

        ParticipationStatusUpdateRequest updateRequest = new ParticipationStatusUpdateRequest();
        updateRequest.setIds(List.of(1L));
        updateRequest.setStatus(ParticipationStatus.CONFIRMED);

        ParticipationRequestDto confirmedRequest = new ParticipationRequestDto();
        confirmedRequest.setId(1L);
        confirmedRequest.setRequester(2L);
        confirmedRequest.setEvent(eventId);
        confirmedRequest.setStatus("CONFIRMED");

        ParticipationStatusUpdateResult updateResult = new ParticipationStatusUpdateResult();
        updateResult.setConfirmedRequests(List.of(confirmedRequest));
        updateResult.setRejectedRequests(List.of());

        when(participationService.updateStatus(anyLong(), anyLong(), any(ParticipationStatusUpdateRequest.class)))
                .thenReturn(updateResult);

        mockMvc.perform(patch("/users/{userId}/events/{eventId}/requests", userId, eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updateResult)));

        verify(participationService).updateStatus(userId, eventId, updateRequest);
    }
}