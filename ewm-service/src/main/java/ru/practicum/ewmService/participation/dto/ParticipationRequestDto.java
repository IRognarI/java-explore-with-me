package ru.practicum.ewmService.participation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParticipationRequestDto {
    private long id;
    private long event;
    private long requester;
    private String status;
    private String created;
}