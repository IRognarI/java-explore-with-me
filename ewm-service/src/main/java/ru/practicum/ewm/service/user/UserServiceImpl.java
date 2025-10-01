package ru.practicum.ewm.service.user;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.userDto.UserRequestDto;
import ru.practicum.ewm.exception.ArrayLinksIsEmptyException;
import ru.practicum.ewm.exception.ObjectDuplicatedException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.interfaces.user.UserService;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.repository.user.JpaUserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    private final JpaUserRepository userRepository;

    @Override
    @Transactional
    public User addUser(UserRequestDto requestDto) {
        if (requestDto.getEmail().isBlank()) {
            throw new ValidationException("Email не должен быть пустым");
        }

        Optional<User> userExists = Optional.ofNullable(
                userRepository.getUserByEmail(requestDto.getEmail().trim().toLowerCase()));

        if (userExists.isPresent()) {
            throw new ObjectDuplicatedException("Пользователь с email=" + requestDto.getEmail() + " - существует");
        }

        if (requestDto.getName().isBlank()) {
            throw new ValidationException("Имя не может быть пустым");
        }

        LOG.info("Для создания пользователя получили имя: {} и email: {}", requestDto.getName(), requestDto.getEmail());

        User userCreate = User.builder()
                .email(requestDto.getEmail())
                .name(requestDto.getName())
                .build();

        User userFromBd = userRepository.save(userCreate);

        LOG.info("Возвращаем пользователя: {}", userFromBd);

        return userFromBd;
    }

    @Override
    public List<User> getUsers(Long[] ids, Integer from, Integer size) {
        if (ids.length < 1) {
            throw new ArrayLinksIsEmptyException("Нужно указать хотя бы один id пользователя, а у вас " + ids.length);
        }

        from = from == null ? 0 : from;
        size = size == null || size == 0 ? 10 : size;

        List<User> userList = userRepository.getUserByParam(ids, from, size);

        LOG.info("Количество объектов в коллекции: {}", userList.size());

        return userList;
    }

    @Override
    @Transactional
    public void userRemove(Long userId) {
        if (userId == null || userId < 0) {
            throw new ValidationException("ID пользователя не может быть { " + userId + " }");
        }

        Optional<User> targetUser = Optional.ofNullable(userRepository.getUserById(userId));

        if (targetUser.isEmpty()) {
            throw new ObjectNotFoundException("Пользователь с ID { " + userId + " } - не найден");
        }

        LOG.info("Пользователь с id { " + userId + " } - успешно удален");

        userRepository.deleteById(targetUser.get().getId());
    }

    public User getUserById(Long userId) {
        if (userId == null || userId < 0) {
            throw new ValidationException("ID пользователя не может быть { " + userId + " }");
        }

        Optional<User> targetUser = Optional.ofNullable(userRepository.getUserById(userId));

        if (targetUser.isEmpty()) {
            throw new ObjectNotFoundException("Пользователь с ID { " + userId + " } - не найден");
        }
        return targetUser.get();
    }

    public boolean userExists(Long userId) {
        return userRepository.existsById(userId);
    }
}
