package ru.practicum.ewmService.user.interfaces;

import ru.practicum.ewmService.user.dto.NewUserRequest;
import ru.practicum.ewmService.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto addUser(NewUserRequest dto);

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    void deleteUser(long userId);
}