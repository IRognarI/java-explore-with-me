package ru.practicum.ewmService.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.ewmService.event.location.Location;

import java.time.LocalDateTime;

/**
 * Объект передачи данных (DTO) для создания нового события.
 * Этот класс инкапсулирует всю необходимую информацию, требуемую для создания события,
 * включая аннотацию, категорию, описание, дату события, место проведения, детали оплаты,
 * лимит участников, настройки модерации и заголовок.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewEventDto {

    @NotNull
    @NotBlank
    @Length(min = 20, max = 2000)
    private String annotation;

    @NotNull
    private Long category;

    @NotNull
    @NotBlank
    @Length(min = 20, max = 7000)
    private String description;

    @NotNull
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull
    private Location location;
    private boolean paid = false;

    @Min(0)
    private int participantLimit = 0;
    private boolean requestModeration = true;

    @NotNull
    @NotBlank
    @Length(min = 3, max = 120)
    private String title;
}