package ru.practicum.ewmService.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewmService.user.dto.NewUserRequest;
import ru.practicum.ewmService.user.dto.UserDto;
import ru.practicum.ewmService.user.interfaces.UserService;

import java.util.List;

/**
 * REST контроллер для управления пользователями.
 * Предоставляет конечные точки для добавления, получения и удаления пользователей с правами администратора.
 */
@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping()
public class UserController {

    private final UserService userService;


    @PostMapping("/admin/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@Valid @RequestBody NewUserRequest dto) {

        log.info("Add user admin POST request: {}", dto);
        return userService.addUser(dto);
    }

    @GetMapping("/admin/users")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUsers(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size) {

        log.info("Get user admin GET request: ids.size={}, start={}, size={}",
                (ids == null ? 0 : ids.size()), from, size);
        return userService.getUsers(ids, from, size);
    }

    @DeleteMapping("/admin/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable long userId) {

        log.info("Delete user admin DELETE request: id={}", userId);
        userService.deleteUser(userId);
    }
}