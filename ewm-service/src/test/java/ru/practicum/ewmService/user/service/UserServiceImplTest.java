package ru.practicum.ewmService.user.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewmService.event.repository.EventRepository;
import ru.practicum.ewmService.exceptions.IntegrityException;
import ru.practicum.ewmService.exceptions.NotFoundException;
import ru.practicum.ewmService.participation.repository.ParticipationRepository;
import ru.practicum.ewmService.user.dto.NewUserRequest;
import ru.practicum.ewmService.user.dto.UserDto;
import ru.practicum.ewmService.user.mapper.UserMapper;
import ru.practicum.ewmService.user.model.User;
import ru.practicum.ewmService.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@Service
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ParticipationRepository participationRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void addUser_whenEmailNotExists_shouldSaveAndReturnUserDto() {
        NewUserRequest dto = new NewUserRequest();
        dto.setEmail("test@example.com");
        dto.setName("Test User");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);

        User user = UserMapper.toUser(dto);
        user.setId(1L);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.addUser(dto);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());
        assertEquals(1L, result.getId());

        verify(userRepository).existsByEmail(dto.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void addUser_whenEmailExists_shouldThrowIntegrityException() {
        NewUserRequest dto = new NewUserRequest();
        dto.setEmail("test@example.com");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        IntegrityException exception = assertThrows(IntegrityException.class, () -> userService.addUser(dto));

        assertEquals("Email already exists: 'test@example.com'", exception.getMessage());

        verify(userRepository).existsByEmail(dto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUsers_whenIdsIsNull_shouldReturnAllUsers() {
        int from = 0;
        int size = 10;

        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        List<User> users = List.of(user1, user2);
        when(userRepository.findAllOrderByIdAsc(PageRequest.of(from, size))).thenReturn(users);

        List<UserDto> result = userService.getUsers(null, from, size);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(userRepository).findAllOrderByIdAsc(PageRequest.of(from, size));
    }

    @Test
    void getUsers_whenIdsIsEmpty_shouldReturnAllUsers() {
        int from = 0;
        int size = 10;

        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        List<User> users = List.of(user1, user2);
        when(userRepository.findAllOrderByIdAsc(PageRequest.of(from, size))).thenReturn(users);

        List<UserDto> result = userService.getUsers(List.of(), from, size);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(userRepository).findAllOrderByIdAsc(PageRequest.of(from, size));
    }

    @Test
    void getUsers_whenIdsProvided_shouldReturnUsersByIds() {
        List<Long> ids = List.of(1L, 2L);
        int from = 0;
        int size = 10;

        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        List<User> users = List.of(user1, user2);
        when(userRepository.findAllByIdInOrderByIdAsc(ids, PageRequest.of(from, size))).thenReturn(users);

        List<UserDto> result = userService.getUsers(ids, from, size);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(userRepository).findAllByIdInOrderByIdAsc(ids, PageRequest.of(from, size));
    }

    @Test
    void deleteUser_whenUserNotExists_shouldThrowNotFoundException() {
        long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.deleteUser(userId));

        assertEquals("User with id=1 not found", exception.getMessage());

        verify(userRepository).existsById(userId);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteUser_whenUserHasEvents_shouldThrowNotFoundException() {
        long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(eventRepository.existsByInitiatorId(userId)).thenReturn(true);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.deleteUser(userId));

        assertEquals("User with id=1 is unavailable to deletion ", exception.getMessage());

        verify(userRepository).existsById(userId);
        verify(eventRepository).existsByInitiatorId(userId);
        verify(participationRepository, never()).existsByRequesterId(anyLong());
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteUser_whenUserHasParticipations_shouldThrowNotFoundException() {
        long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(eventRepository.existsByInitiatorId(userId)).thenReturn(false);
        when(participationRepository.existsByRequesterId(userId)).thenReturn(true);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.deleteUser(userId));

        assertEquals("User with id=1 is unavailable to deletion ", exception.getMessage());

        verify(userRepository).existsById(userId);
        verify(eventRepository).existsByInitiatorId(userId);
        verify(participationRepository).existsByRequesterId(userId);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteUser_whenUserExistsAndHasNoEventsOrParticipations_shouldDeleteUser() {
        long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(eventRepository.existsByInitiatorId(userId)).thenReturn(false);
        when(participationRepository.existsByRequesterId(userId)).thenReturn(false);

        assertDoesNotThrow(() -> userService.deleteUser(userId));

        verify(userRepository).existsById(userId);
        verify(eventRepository).existsByInitiatorId(userId);
        verify(participationRepository).existsByRequesterId(userId);
        verify(userRepository).deleteById(userId);
    }
}