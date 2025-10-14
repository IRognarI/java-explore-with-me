package ru.practicum.ewmService.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewmService.event.repository.EventRepository;
import ru.practicum.ewmService.exceptions.IntegrityException;
import ru.practicum.ewmService.exceptions.NotFoundException;
import ru.practicum.ewmService.participation.repository.ParticipationRepository;
import ru.practicum.ewmService.user.dto.NewUserRequest;
import ru.practicum.ewmService.user.dto.UserDto;
import ru.practicum.ewmService.user.interfaces.UserService;
import ru.practicum.ewmService.user.mapper.UserMapper;
import ru.practicum.ewmService.user.model.User;
import ru.practicum.ewmService.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ParticipationRepository participationRepository;

    @Override
    public UserDto addUser(NewUserRequest dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IntegrityException("Email already exists: '%s'".formatted(dto.getEmail()));
        }
        User user = UserMapper.toUser(dto);
        user = userRepository.save(user);
        UserDto result = UserMapper.toUserDto(user);
        log.info("Add user returns: {}", result);
        return result;
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {

        List<UserDto> result;
        if (ids == null || ids.isEmpty()) {
            result = userRepository.findAllOrderByIdAsc(PageRequest.of(from, size))
                    .stream()
                    .map(UserMapper::toUserDto)
                    .toList();
        } else {
            result = userRepository.findAllByIdInOrderByIdAsc(ids, PageRequest.of(from, size))
                    .stream()
                    .map(UserMapper::toUserDto)
                    .toList();
        }
        log.info("Get users request returns {} records", result.size());
        return result;
    }

    @Override
    public void deleteUser(long userId) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=%d not found".formatted(userId));
        }

        if (eventRepository.existsByInitiatorId(userId) ||
                participationRepository.existsByRequesterId(userId)) {
            throw new NotFoundException("User with id=%d is unavailable to deletion ".formatted(userId));
        }

        userRepository.deleteById(userId);
        log.info("Delete user with id={} operation has done", userId);
    }
}