package ru.practicum.ewm.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.dto.userDto.UserRequestDto;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.repository.JpaUserRepository;
import ru.practicum.ewm.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTestMock {

    @Mock
    private JpaUserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequestDto requestDto;
    private UserRequestDto requestDto_2;
    private UserRequestDto requestDto_3;
    private List<User> defaultUserList;

    private User userDefault;
    private User userDefault_2;
    private User userDefault_3;

    @BeforeEach
    public void setUp() {
        requestDto = UserRequestDto.builder()
                .email("emailOne@mail.ru")
                .name("Name#1")
                .build();

        requestDto_2 = UserRequestDto.builder()
                .email("")
                .name("Name#1")
                .build();

        requestDto_3 = UserRequestDto.builder()
                .email("emailOne@mail.ru")
                .name("")
                .build();

        userDefault = User.builder()
                .id(1L)
                .email("emailOne@mail.ru")
                .name("Name#1")
                .build();

        userDefault_2 = User.builder()
                .id(2L)
                .email("emailTwo@mail.ru")
                .name("Name#2")
                .build();

        userDefault_3 = User.builder()
                .id(3L)
                .email("emailThree@mail.ru")
                .name("Name#3")
                .build();

        defaultUserList = new ArrayList<>(List.of(userDefault, userDefault_2, userDefault_3));

        Mockito.lenient().when(userRepository.save(Mockito.any(User.class))).thenReturn(userDefault);
    }

    @Test
    public void addUser_Correct() {
        User user = userService.addUser(requestDto);

        Assertions.assertNotNull(user);
        Assertions.assertEquals(userDefault.getId(), user.getId(), "ID не был присвоен");
    }

    @Test
    public void addUser_shouldBeThrowValidationException_BecauseEmailIsBlank() {

        Assertions.assertThrows(ValidationException.class, () -> userService.addUser(requestDto_2));
    }

    @Test
    public void addUser_shouldBeThrowValidationException_BecauseNameIsBlank() {

        Assertions.assertThrows(ValidationException.class, () -> userService.addUser(requestDto_3));
    }

    @Test
    public void getUsers_shouldThreeObjectInCollection() {
        Long[] ids = {1L, 2L, 3L};

        int from = 0;
        int size = 3;

        Mockito.when(userRepository.getUserByParam(ids, from, size)).thenReturn(defaultUserList);

        List<User> userList = userService.getUsers(ids, from, size);

        Assertions.assertEquals(defaultUserList.size(), userList.size());
    }
}
