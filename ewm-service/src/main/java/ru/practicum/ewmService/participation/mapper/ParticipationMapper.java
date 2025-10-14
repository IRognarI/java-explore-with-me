package ru.practicum.ewmService.participation.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmService.dateTimeFormatter.Formatter;
import ru.practicum.ewmService.participation.dto.ParticipationRequestDto;
import ru.practicum.ewmService.participation.model.Participation;

import java.util.Collection;
import java.util.List;

@UtilityClass
public class ParticipationMapper {

    public ParticipationRequestDto toDto(Participation participation) {
        return new ParticipationRequestDto(
                participation.getId(),
                participation.getEvent().getId(),
                participation.getRequester().getId(),
                participation.getStatus().name(),
                participation.getCreated().format(Formatter.FORMATTER));
    }

    public List<ParticipationRequestDto> toDtos(Collection<Participation> participationCollection) {
        return participationCollection.stream().map(ParticipationMapper::toDto).toList();
    }
}