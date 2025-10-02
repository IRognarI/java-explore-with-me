package ru.practicum.ewm.service.event;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.eventDto.eventDtoRequest.EventDtoRequest;
import ru.practicum.dto.eventDto.location.Location;
import ru.practicum.ewm.enums.State;
import ru.practicum.ewm.exception.DateTimeCheckException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.exception.StateValidationException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.interfaces.event.EventService;
import ru.practicum.ewm.mapper.Mapper;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.user.User;
import ru.practicum.ewm.repository.event.EventRepository;
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

    private final JpaEventRepository jpaEventRepository;
    private final UserServiceImpl userService;
    private final CategoryServiceImpl categoryService;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public Event addEvent(EventDtoRequest eventDtoRequest, Long userId) {
        idValidate(userId);

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

        Event eventFromDb = jpaEventRepository.save(newEvent);

        LOG.info("Добавили мероприятие: " + eventFromDb);

        return eventFromDb;
    }

    @Override
    public List<Event> getEvents(Integer from, Integer size, Long userId) {
        from = from == null ? 0 : from;
        size = size == null ? 10 : size;

        idValidate(userId);
        boolean userExists = checkUserExists(userId);

        if (!userExists) throw new ObjectNotFoundException("Пользователь с ID { " + userId + " } - не найден");

        List<Event> eventList = jpaEventRepository.getInitiatorEvent(from, size, userId);

        LOG.info("Вернули коллекцию мероприятий, размером: {}", eventList.size());

        return eventList;
    }

    @Override
    public Event getTargetEvent(Long userId, Long eventId) {
        idValidate(userId);
        idValidate(eventId);

        boolean userExists = checkUserExists(userId);

        if (!userExists) throw new ObjectNotFoundException("Пользователь с ID { " + userId + " } - не найден");

        Optional<Event> eventExists = Optional.ofNullable(jpaEventRepository.getEventByIdAndInitiator_Id(eventId, userId));

        if (eventExists.isEmpty()) throw new ObjectNotFoundException("Мероприятие с ID=" + eventId + ", где" +
                " организатор пользователь с ID=" + userId + " - не найдено");

        LOG.info("Вернули Event: " + eventExists.get());

        return eventExists.get();
    }

    @Override
    @Transactional
    public Event updateEvent(Long userId, Long eventId, EventDtoRequest eventDtoRequest) {
        idValidate(userId);
        idValidate(eventId);

        if (eventDtoRequest == null) throw new ValidationException("Не достаточно данных для обновления мероприятия");

        Optional<Event> eventForUpdate = Optional.ofNullable(jpaEventRepository.findEventForUpdate(userId, eventId));

        if (eventForUpdate.isEmpty()) throw new ObjectNotFoundException("Мероприятие с ID=" + eventId + ", где" +
                " организатор пользователь с ID=" + userId + " - не найдено");

        Category category = categoryService.getCategory(eventDtoRequest.getCategory().longValue());

        LocalDateTime validateEventDate = LocalDateTime.now().plusHours(2);
        LocalDateTime newEventDate = null;
        if (eventDtoRequest.getEventDate() != null) {

            if (eventDtoRequest.getEventDate().isBefore(validateEventDate)) {
                throw new DateTimeCheckException("Начало мероприятия не может быть раньше, чем через два часа от текущего момента");

            } else {
                newEventDate = eventDtoRequest.getEventDate();
            }
        } else {
            newEventDate = eventForUpdate.get().getEventDate();
        }

        String newAnnotation = eventDtoRequest.getAnnotation() != null ? eventDtoRequest.getAnnotation() :
                eventForUpdate.get().getAnnotation();

        String newDescription = eventDtoRequest.getDescription() != null ? eventDtoRequest.getDescription() :
                eventForUpdate.get().getDescription();

        Location newLocation = eventDtoRequest.getLocation() != null ? eventDtoRequest.getLocation() :
                new Location(eventForUpdate.get().getLat(), eventForUpdate.get().getLon());

        Boolean newPaid = eventDtoRequest.getPaid() != null ? eventDtoRequest.getPaid() : eventForUpdate.get().getPaid();

        int newParticipantLimit = eventDtoRequest.getParticipantLimit() != null ? eventDtoRequest.getParticipantLimit() :
                eventForUpdate.get().getParticipantLimit();

        Boolean newRequestModeration = eventDtoRequest.getRequestModeration() != null ? eventDtoRequest.getRequestModeration() :
                eventForUpdate.get().getRequestModeration();

        String newTitle = eventDtoRequest.getTitle() != null ? eventDtoRequest.getTitle() : eventForUpdate.get().getTitle();

        State newState = eventDtoRequest.getStateAction() != null &&
                eventDtoRequest.getStateAction().equals("PUBLISH_EVENT") ? State.PENDING : State.CANCELED;

        Event updateEvent = jpaEventRepository.save(eventForUpdate.get().toBuilder()
                .annotation(newAnnotation)
                .category(category)
                .description(newDescription)
                .eventDate(newEventDate)
                .lat(newLocation.getLat())
                .lon(newLocation.getLon())
                .paid(newPaid)
                .participantLimit(newParticipantLimit)
                .requestModeration(newRequestModeration)
                .state(newState)
                .title(newTitle)
                .build());

        LOG.info("Event с ID=" + eventId + " - обновлен: " + updateEvent);

        return updateEvent;
    }

    @Override
    public List<Event> searchEventsWithParams(Long[] userIds, String[] states, Long[] categoriesIds,
                                              LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {

        List<Event> eventList = eventRepository.getEventsWithParams(userIds, states, categoriesIds, rangeStart, rangeEnd, from, size);

        LOG.info("Вернули список events размером: " + eventList.size());

        return eventList;
    }

    @Override
    @Transactional
    public Event updateEventByAdmin(Long eventId, EventDtoRequest request) {
        idValidate(eventId);

        if (request == null) {
            throw new ValidationException("Не достаточно данных для обновления мероприятия");
        }

        Optional<Event> targetEvent = Optional.ofNullable(jpaEventRepository.getEventById(eventId));

        if (targetEvent.isEmpty()) {
            throw new ObjectNotFoundException("Мероприятие с ID " + eventId + " не найдено");
        }

        String newAnnotation = request.getAnnotation() != null ? request.getAnnotation() : targetEvent.get().getAnnotation();
        Long categoryId = request.getCategory() != null ? request.getCategory().longValue() : targetEvent.get().getCategory().getId();
        String newDescription = request.getDescription() != null ? request.getDescription() : targetEvent.get().getDescription();

        LocalDateTime updateEventDate = null;

        LocalDateTime publishedOn = LocalDateTime.now();
        LocalDateTime minDateTime = publishedOn.plusHours(1);

        if (request.getEventDate() != null) {

            if (request.getEventDate().isBefore(minDateTime)) {
                throw new DateTimeCheckException("Время начала мероприятия должно быть не ранее чем за час от даты публикации");

            } else {
                updateEventDate = request.getEventDate();
            }
        } else {
            updateEventDate = targetEvent.get().getEventDate();
        }

        Location newLocation = request.getLocation() != null ? request.getLocation() :
                new Location(targetEvent.get().getLat(), targetEvent.get().getLon());

        Boolean newPaid = request.getPaid() != null ? request.getPaid() : targetEvent.get().getPaid();
        Integer newParticipantLimit = request.getParticipantLimit() != null ? request.getParticipantLimit() :
                targetEvent.get().getParticipantLimit();
        Boolean newRequestModeration = request.getRequestModeration() != null ? request.getRequestModeration() :
                targetEvent.get().getRequestModeration();

        State actualState = targetEvent.get().getState();

        if (actualState.equals(State.PUBLISHED)) {
            throw new StateValidationException("Нельзя редактировать мероприятие в статусе " + actualState);
        }

        String newTitle = request.getTitle() != null ? request.getTitle() : targetEvent.get().getTitle();

        Event updateEvent = jpaEventRepository.save(targetEvent.get().toBuilder()
                .annotation(newAnnotation)
                .category(categoryService.getCategory(categoryId))
                .description(newDescription)
                .eventDate(updateEventDate)
                .lat(newLocation.getLat())
                .lon(newLocation.getLon())
                .paid(newPaid)
                .participantLimit(newParticipantLimit)
                .requestModeration(newRequestModeration)
                .state(actualState)
                .title(newTitle)
                .build());

        LOG.info("Admin обновил event: " + updateEvent);

        return updateEvent;
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

    private void idValidate(Long id) {
        if (id == null || id < 1) throw new ValidationException("ID не может быть " + id);
    }
}
