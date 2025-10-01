package ru.practicum.ewm.model.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.enums.State;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.user.User;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder(toBuilder = true)
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"state", "paid", "eventDate", "participantLimit"})
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "event_id", nullable = false)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotNull(message = "Укажите название мероприятия")
    private String title;

    @Column(nullable = false)
    @NotNull(message = "Укажите краткое описание для мероприятия")
    private String annotation;

    @NotNull(message = "Описание мероприятия не может быть пустым")
    @Column(name = "description_ev", nullable = false, length = 3000)
    private String description;

    @NotNull(message = "Укажите дату начала мероприятия")
    @Future(message = "Дата начала мероприятия может быть только в будущем")
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @NotNull(message = "Категория мероприятия должна быть обязательно заполнена")
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "state_ev", nullable = false)
    @NotNull(message = "Укажите статус мероприятия")
    private State state;

    @NotNull(message = "Дата создания мероприятия должна быть указана")
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @NotNull(message = "Для корректного получения местоположения проведения мероприятия, \"широта\" должна быть указана")
    @Column(nullable = false)
    private Double lat;

    @NotNull(message = "Для корректного получения местоположения проведения мероприятия, \"долгота\" должна быть указана")
    @Column(nullable = false)
    private Double lon;

    @NotNull(message = "Нужно указать мероприятие платное или нет")
    @Column(nullable = false)
    private Boolean paid;

    @PositiveOrZero(message = "Количество участников не может быть отрицательным")
    @NotNull(message = "Введите ограничение на количество участников. Если ограничения нет, введите \"0\"")
    @Column(name = "participant_limit", nullable = false)
    private Integer participantLimit;

    @NotNull(message = "Укажите нужно ли ручное рассмотрение заявки")
    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration;

    @NotNull(message = "Организатор мероприятия должен быть указан")
    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;
}
