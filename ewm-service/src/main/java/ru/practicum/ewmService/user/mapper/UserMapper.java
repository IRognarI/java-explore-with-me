package ru.practicum.ewmService.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmService.user.dto.NewUserRequest;
import ru.practicum.ewmService.user.dto.UserDto;
import ru.practicum.ewmService.user.dto.UserShortDto;
import ru.practicum.ewmService.user.model.User;

@UtilityClass
public class UserMapper {

    public User toUser(NewUserRequest dto) {
        return new User(null, dto.getName(), dto.getEmail());
    }

    public UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public UserShortDto toUserShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }
}