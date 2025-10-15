package ru.practicum.statsDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

/**
 * Объект передачи данных (DTO) для фиксации и передачи статистики просмотров.
 * Этот класс представляет собой одну запись просмотра с информацией о приложении,
 * URI, IP-адресе и времени просмотра.
 *
 * <p>Ограничения:
 * <ul>
 *   <li>app: не должен быть null или пустым, длина от 1 до 64 символов</li>
 *   <li>uri: не должен быть null или пустым, длина от 1 до 64 символов</li>
 *   <li>ip: не должен быть null или пустым, длина от 1 до 64 символов</li>
 *   <li>timestamp: не должен быть null, формат "yyyy-MM-dd HH:mm:ss"</li>
 * </ul>
 *
 * @author Ваше Имя
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewHitDto {

    @NotNull
    @NotBlank
    @Length(min = 1, max = 64)
    private String app;

    @NotNull
    @NotBlank
    @Length(min = 1, max = 64)
    private String uri;

    @NotNull
    @NotBlank
    @Length(min = 1, max = 64)
    private String ip;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}