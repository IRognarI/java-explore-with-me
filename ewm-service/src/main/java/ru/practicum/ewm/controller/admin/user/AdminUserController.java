package ru.practicum.ewm.controller.admin.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.userDto.UserDto;
import ru.practicum.dto.userDto.UserRequestDto;
import ru.practicum.ewm.interfaces.user.UserService;
import ru.practicum.ewm.mapper.Mapper;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminUserController {
    private static final Logger LOG = LoggerFactory.getLogger(AdminUserController.class);
    private static final String USER_PATH = "/users";

    private final UserService userService;

    @PostMapping(USER_PATH)
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@Valid @RequestBody UserRequestDto requestDto) {
        LOG.info("Получен POST запрос в endPoint \"/admin/users\" для создания пользователя. Email: {}, Name: {}",
                requestDto.getEmail(), requestDto.getName());

        return Mapper.toUserDto(userService.addUser(requestDto));
    }

    @GetMapping(USER_PATH)
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUsers(
            @RequestParam(name = "ids") Long[] ids,
            @RequestParam(name = "from") Integer from,
            @RequestParam(name = "size") Integer size
    ) {
        LOG.info("""
                Получен GET запрос в endPoint "/admin/users" для поиска пользователей с параметрами:
                ID пользователей: {}
                Кол-во пропускаемых элементов: {}
                Ограничение для вывода: {}
                """, ids, from, size);

        return userService.getUsers(ids, from, size)
                .stream()
                .map(Mapper::toUserDto)
                .toList();
    }

    @DeleteMapping(USER_PATH + "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void userRemove(@PathVariable(name = "id") Long id) {
        LOG.info("Получен DELETE запрос в endPoint \"/admin/users/" + id + "\". Для удаления пользователя с id: " + id);

        userService.userRemove(id);
    }
}
