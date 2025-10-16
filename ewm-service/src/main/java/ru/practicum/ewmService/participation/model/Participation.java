package ru.practicum.ewmService.participation.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
import ru.practicum.ewmService.dateTimeFormatter.Formatter;
import ru.practicum.ewmService.event.model.Event;
import ru.practicum.ewmService.participation.enums.ParticipationStatus;
import ru.practicum.ewmService.user.model.User;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Сущность, представляющая запрос на участие в событии.
 * Этот класс сопоставляется с таблицей "participation" в базе данных и включает
 * информацию о событии, запрашивающем пользователе, статусе и времени создания.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "participation")
public class Participation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;

    @Enumerated(EnumType.STRING)
    private ParticipationStatus status;

    @JsonFormat(pattern = Formatter.PATTERN)
    private LocalDateTime created;


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Participation that = (Participation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}