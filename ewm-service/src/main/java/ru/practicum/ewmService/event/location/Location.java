package ru.practicum.ewmService.event.location;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Представляет географическое местоположение с координатами широты и долготы.
 * Этот класс используется как встраиваемая сущность в JPA для хранения данных о местоположении.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Location {
    private float lat;
    private float lon;
}