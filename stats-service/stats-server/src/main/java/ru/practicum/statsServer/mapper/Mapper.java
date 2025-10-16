package ru.practicum.statsServer.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.statsDto.NewHitDto;
import ru.practicum.statsServer.model.Hit;

@UtilityClass
public class Mapper {
    public Hit toEntity(NewHitDto dto) {
        return new Hit(null, dto.getApp(), dto.getUri(), dto.getIp(), dto.getTimestamp());
    }
}