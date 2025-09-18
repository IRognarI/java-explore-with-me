package ru.practicum.exploreWithMe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Класс-сущность, представляющий обращение к эндпоинту.
 * Этот класс сопоставляется с таблицей "endpoint_hits" в базе данных и содержит информацию
 * о запросах к определенным эндпоинтам, включая название приложения, URI, IP-адрес
 * и временную метку запроса.
 *
 * <p>Поля:</p>
 * <ul>
 *     <li>{@code id} - Уникальный идентификатор обращения к эндпоинту (автогенерируемый)</li>
 *     <li>{@code app} - Название приложения (максимум 100 символов, не может быть null)</li>
 *     <li>{@code uri} - URI эндпоинта (не может быть null)</li>
 *     <li>{@code ip} - IP-адрес клиента (не может быть null)</li>
 *     <li>{@code timestamp} - Временная метка запроса (не может быть null, не может быть в прошлом)</li>
 * </ul>
 *
 * <p>Этот класс использует аннотации Lombok для генерации геттеров, сеттеров, toString и конструкторов.</p>
 *
 * @see jakarta.persistence.Entity
 * @see jakarta.persistence.Table
 * @see jakarta.persistence.Id
 * @see jakarta.persistence.GeneratedValue
 * @see jakarta.persistence.Column
 * @see jakarta.validation.constraints.FutureOrPresent
 */

@Getter
@Setter
@Entity
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "endpoint_hits")
public class EndpointHit {
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "app", nullable = false, length = 100)
    private String app;

    @Column(name = "uri", nullable = false)
    private String uri;

    @Column(name = "ip", nullable = false)
    private String ip;

    @Column(name = "timestamp", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    @FutureOrPresent(message = "Дата не может быть в прошлом")
    private LocalDateTime timestamp;

    @Override
    public final boolean equals(Object object) {
        if (this == object) return true;
        if (object == null) return false;
        Class<?> oEffectiveClass = object instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : object.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        EndpointHit that = (EndpointHit) object;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
