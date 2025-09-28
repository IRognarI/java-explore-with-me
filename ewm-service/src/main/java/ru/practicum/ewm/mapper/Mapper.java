package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.userDto.UserDto;
import ru.practicum.ewm.model.user.User;

@UtilityClass
public class Mapper {

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
