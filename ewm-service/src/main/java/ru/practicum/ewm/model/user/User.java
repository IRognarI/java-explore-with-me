package ru.practicum.ewm.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.model.event.Event;

import java.util.List;

@EqualsAndHashCode(of = {"email"})
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
@Entity
@Table(name = "users")
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Email(message = "Укажите корректный email адрес")
    @NotNull(message = "Email адрес обязателен для заполнения")
    @Column(unique = true, nullable = false, length = 60)
    private String email;

    @NotNull(message = "Укажите имя")
    @Column(nullable = false, length = 100)
    private String name;

    @OneToMany(mappedBy = "initiator", targetEntity = Event.class, fetch = FetchType.LAZY)
    private List<Event> eventList;
}



