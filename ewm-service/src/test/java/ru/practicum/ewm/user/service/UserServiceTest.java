package ru.practicum.ewm.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.userDto.UserRequestDto;
import ru.practicum.ewm.exception.UserDuplicatedException;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.service.UserServiceImpl;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceTest {

    @Autowired
    private UserServiceImpl userService;

    private UserRequestDto userRequestDto;
    private UserRequestDto userRequestDto_2;
    private UserRequestDto userRequestDto_3;
    private UserRequestDto userRequestDto_4;
    private List<User> userList;

    @BeforeEach
    public void setUp() {

        userRequestDto = UserRequestDto.builder()
                .name("Name#1")
                .email("email#1@mail.ru")
                .build();

        userRequestDto_2 = UserRequestDto.builder()
                .name("Name#2")
                .email("email#2@mail.ru")
                .build();

        userRequestDto_3 = UserRequestDto.builder()
                .name("Name#3")
                .email("email#3@mail.ru")
                .build();

        userRequestDto_4 = UserRequestDto.builder()
                .name("Name#3")
                .email(userRequestDto_3.getEmail())
                .build();
    }

    @Test
    public void addUser_ShouldThrowWhenUserExistsWithEmail() {
        userService.addUser(userRequestDto_3);

        Assertions.assertThrows(UserDuplicatedException.class, () -> userService.addUser(userRequestDto_4));
    }

    @Test
    public void getUsers_ShouldTwoEntityWhenSizeTwo() {
        User user1 = userService.addUser(userRequestDto);
        User user2 = userService.addUser(userRequestDto_2);
        User user3 = userService.addUser(userRequestDto_3);

        Long[] ids = {user1.getId(), user2.getId(), user3.getId()};

        userList = userService.getUsers(ids, 0, 2);

        Assertions.assertEquals(2, userList.size());
    }

    @Test
    public void getUsers_ShouldTwoEntityWhenFromOneAndSizeIsNull() {
        User user1 = userService.addUser(userRequestDto);
        User user2 = userService.addUser(userRequestDto_2);
        User user3 = userService.addUser(userRequestDto_3);

        Long[] ids = {user1.getId(), user2.getId(), user3.getId()};

        userList = userService.getUsers(ids, 1, null);

        Assertions.assertEquals(2, userList.size());
    }

    @Test
    public void getUsers_ShouldThreeEntityWhenFromIsNullAndSizeIsNull() {
        User user1 = userService.addUser(userRequestDto);
        User user2 = userService.addUser(userRequestDto_2);
        User user3 = userService.addUser(userRequestDto_3);

        Long[] ids = {user1.getId(), user2.getId(), user3.getId()};

        userList = userService.getUsers(ids, null, null);

        Assertions.assertEquals(3, userList.size());
    }

    @Test
    public void deleteUserById_Correct() {
        User user1 = userService.addUser(userRequestDto);

        Long[] ids_1 = {user1.getId()};

        List<User> usersListNoEmpty = userService.getUsers(ids_1, 0, 0);

        Assertions.assertFalse(usersListNoEmpty.isEmpty(), "В базе нет объекта с id " + user1.getId());

        userService.userRemove(user1.getId());

        Long[] ids = {user1.getId()};

        List<User> users = userService.getUsers(ids, 0, 0);

        Assertions.assertTrue(users.isEmpty(), "Объект с id " + user1.getId() + " -  не был удален");
    }

    @Test
    public void deleteUserById_WhenUserNotPresent() {

        Assertions.assertThrows(UserNotFoundException.class,
                () -> userService.userRemove(2343L));
    }
}
