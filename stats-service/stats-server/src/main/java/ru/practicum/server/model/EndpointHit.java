package ru.practicum.server.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

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
 */

@Getter
@Setter
@Entity
@ToString
@EqualsAndHashCode(of = {"app", "uri"})
@Builder(toBuilder = true)
@NoArgsConstructor
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

    @Column(name = "ip", nullable = false, length = 60)
    private String ip;

    @Column(name = "timestamp_db", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime timestamp;
}
