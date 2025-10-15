package ru.practicum.ewmService.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewmService.category.model.Category;
import ru.practicum.ewmService.dateTimeFormatter.Formatter;
import ru.practicum.ewmService.event.enums.EventState;
import ru.practicum.ewmService.event.location.Location;
import ru.practicum.ewmService.user.model.User;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Сущность события в системе.
 * Этот класс отображается на таблицу "events" в базе данных и включает все соответствующие поля и связи
 * связанные с событием, такие как категория, инициатор, местоположение и различные метаданные.
 * Также предоставляет вспомогательные методы для управления лимитами участников и проверки равенства.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    private String annotation;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @ToString.Exclude
    private String description;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @ToString.Exclude
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "lat", column = @Column(name = "location_lat")),
            @AttributeOverride(name = "lon", column = @Column(name = "location_lon"))
    })
    private Location location;

    @ToString.Exclude
    private boolean paid;

    @ToString.Exclude
    @Column(name = "participant_limit")
    private int participantLimit;

    @ToString.Exclude
    @Column(name = "confirmed_requests")
    private int confirmedRequests;

    @ToString.Exclude
    @Column(name = "request_moderation")
    private boolean requestModeration;

    @ToString.Exclude
    @JsonFormat(pattern = Formatter.PATTERN)
    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @ToString.Exclude
    @JsonFormat(pattern = Formatter.PATTERN)
    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Enumerated(EnumType.STRING)
    private EventState state;

    private String title;


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public boolean isLimitExpired() {
        return (participantLimit != 0) && (confirmedRequests >= participantLimit);
    }

    public void increaseConfirmedRequests() {
        confirmedRequests += 1;
    }

    public void decreaseConfirmedRequests() {
        confirmedRequests -= 1;
    }
}