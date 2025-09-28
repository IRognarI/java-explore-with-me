package ru.practicum.ewm.user.controller.admin;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.dto.userDto.UserDto;
import ru.practicum.dto.userDto.UserRequestDto;
import ru.practicum.ewm.controller.admin.AdminController;
import ru.practicum.ewm.interfaces.UserService;
import ru.practicum.ewm.model.user.User;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTestMock {

    @Mock
    private UserService userService;

    @InjectMocks
    AdminController adminController;

    private UserRequestDto requestDto;
    private UserRequestDto requestDto_2;

    private User userDefault;

    private Validator validator;

    @BeforeEach
    public void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();

        requestDto = UserRequestDto.builder()
                .email("testmail@Gmail.com")
                .name("name#1")
                .build();

        requestDto_2 = UserRequestDto.builder()
                .email(null)
                .name("name#1")
                .build();

        userDefault = User.builder()
                .id(1L)
                .email("testmail@Gmail.com")
                .name("name#1")
                .build();

        Mockito.lenient().when(userService.addUser(Mockito.any(UserRequestDto.class)))
                .thenReturn(userDefault);

    }

    @Test
    public void addUser_Correct() {
        UserDto userDto = adminController.addUser(requestDto);

        Assertions.assertEquals(userDefault.getId(), userDto.getId());
        Assertions.assertEquals(userDefault.getEmail(), userDto.getEmail());
        Assertions.assertEquals(userDefault.getName(), userDto.getName());
    }
}
