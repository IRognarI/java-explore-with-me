package ru.practicum.ewm.interfaces.user;

import ru.practicum.dto.userDto.UserRequestDto;
import ru.practicum.ewm.model.user.User;

import java.util.List;

public interface UserService {
    User addUser(UserRequestDto requestDto);

    List<User> getUsers(Long[] ids, Integer from, Integer size);

    void userRemove(Long userId);
}
