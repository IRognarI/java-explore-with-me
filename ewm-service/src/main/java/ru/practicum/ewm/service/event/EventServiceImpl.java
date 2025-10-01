package ru.practicum.ewm.service.event;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.eventDto.eventDtoRequest.EventDtoRequest;
import ru.practicum.ewm.enums.State;
import ru.practicum.ewm.exception.DateTimeCheckException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.interfaces.event.EventService;
import ru.practicum.ewm.mapper.Mapper;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.repository.event.JpaEventRepository;
import ru.practicum.ewm.service.category.CategoryServiceImpl;
import ru.practicum.ewm.service.user.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final static Logger LOG = LoggerFactory.getLogger(EventServiceImpl.class);

    private final JpaEventRepository eventRepository;
    private final UserServiceImpl userService;
    private final CategoryServiceImpl categoryService;

    @Override
    @Transactional
    public Event addEvent(EventDtoRequest eventDtoRequest, Long userId) {
        if (userId == null || userId < 1) throw new ValidationException("ID пользователя не может быть " + userId);

        if (eventDtoRequest == null) throw new ValidationException("Не достаточно данных для создания мероприятия");

        Optional<User> userExists = Optional.of(getUserIfExists(userId));

        Optional<Category> categoryExists = Optional.of(getCategoryIfExists(eventDtoRequest.getCategory().longValue()));

        LocalDateTime targetDateTime = LocalDateTime.now().plusHours(2);
        boolean checkEventDate = eventDtoRequest.getEventDate().isBefore(targetDateTime);

        if (checkEventDate)
            throw new DateTimeCheckException("Дата начала мероприятия должна быть не раньше: " + targetDateTime);

        Event newEvent = Mapper.requestDtoToEvent(eventDtoRequest, categoryExists.get(), userExists.get())
                .toBuilder()
                .state(State.PENDING)
                .createdOn(LocalDateTime.now())
                .build();

        Event eventFromDb = eventRepository.save(newEvent);

        LOG.info("Добавили мероприятие: " + eventFromDb);

        return eventFromDb;
    }

    @Override
    public List<Event> getEvents(Integer from, Integer size, Long userId) {
        from = from == null ? 0 : from;
        size = size == null ? 10 : size;

        boolean userExists = checkUserExists(userId);

        if (!userExists) throw new ObjectNotFoundException("Пользователь с ID { " + userId + " } - не найден");

        List<Event> eventList = eventRepository.getInitiatorEvent(from, size, userId);

        LOG.info("Вернули коллекцию мероприятий, размером: {}", eventList.size());

        return eventList;
    }

    @Override
    public Event getTargetEvent(Long userId, Long eventId) {
        if (userId == null || userId < 1) throw new ValidationException("ID пользователя не может быть " + userId);

        if (eventId == null || eventId < 1) throw new ValidationException("ID мероприятия не может быть " + eventId);

        boolean userExists = checkUserExists(userId);

        if (!userExists) throw new ObjectNotFoundException("Пользователь с ID { " + userId + " } - не найден");

        Optional<Event> eventExists = Optional.ofNullable(eventRepository.getEventByIdAndInitiator_Id(eventId, userId));

        if (eventExists.isEmpty()) throw new ObjectNotFoundException("Мероприятие с ID=" + eventId + ", где" +
                " организатор пользователь с ID=" + userId + " - не найдено");

        LOG.info("Вернули Event: " + eventExists.get());

        return eventExists.get();
    }

    private User getUserIfExists(Long userId) {
        Optional<User> userExists = Optional.ofNullable(userService.getUserById(userId));

        if (userExists.isEmpty()) {
            throw new ObjectNotFoundException("Пользователь с ID { " + userId + " } - не найден");

        } else {
            return userExists.get();
        }
    }

    private boolean checkUserExists(Long userId) {
        return userService.userExists(userId);
    }

    private Category getCategoryIfExists(Long categoryId) {
        Optional<Category> categoryExists = Optional.ofNullable(categoryService.getCategory(categoryId));

        if (categoryExists.isEmpty()) {
            throw new ObjectNotFoundException("Категория с ID { " + categoryId + " } - не найдена");

        } else {
            return categoryExists.get();
        }
    }
}
