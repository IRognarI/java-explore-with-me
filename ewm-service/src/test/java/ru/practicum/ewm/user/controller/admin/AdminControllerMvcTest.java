package ru.practicum.ewm.user.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.userDto.UserRequestDto;
import ru.practicum.ewm.controller.admin.AdminController;
import ru.practicum.ewm.interfaces.UserService;
import ru.practicum.ewm.model.user.User;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
public class AdminControllerMvcTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    private UserRequestDto requestDto;
    private UserRequestDto requestDto_2;

    @BeforeEach
    public void setUp() {
        requestDto = UserRequestDto.builder()
                .email(null)
                .name("Name#1")
                .build();

        requestDto_2 = UserRequestDto.builder()
                .email("testemail@gmail.com")
                .name(null)
                .build();
    }

    @Test
    public void addUser_ShouldReturnConstraintViolationException_WhenEmailIsNull() throws Exception {
        mvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUser_ShouldReturnConstraintViolationException_WhenNameIsNull() throws Exception {
        mvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto_2)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getUsers_shouldThreeUsersWhenLimitIsZeroAndFromIsZero() throws Exception {

        User user1 = User.builder().id(1L).name("name#1").email("someEmail#1@mail.ru").build();
        User user2 = User.builder().id(2L).name("name#2").email("someEmail#2@mail.ru").build();
        User user3 = User.builder().id(3L).name("name#3").email("someEmail#3@mail.ru").build();

        List<User> userList = new ArrayList<>(List.of(user1, user2, user3));

        long id_1 = user1.getId();
        long id_2 = user2.getId();
        long id_3 = user3.getId();

        Long[] ids = {id_1, id_2, id_3};

        Integer from = 0;
        Integer size = 0;

        Mockito.when(userService.getUsers(ids, from, size)).thenReturn(userList);

        String[] idsString = {String.valueOf(ids[0]), String.valueOf(ids[1]), String.valueOf(ids[2])};

        String fromToString = String.valueOf(from);
        String sizeToString = String.valueOf(size);

        mvc.perform(get("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("ids", idsString)
                        .param("from", fromToString)
                        .param("size", sizeToString))
                .andExpect(status().isOk());
    }
}
