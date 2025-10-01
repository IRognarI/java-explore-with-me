package ru.practicum.dto.eventDto.eventDtoRequest;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.dto.eventDto.location.Location;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EventDtoRequest {
    @NotNull(message = "Заполните краткое описание мероприятия")
    @NotBlank(message = "Не корректно заполнено краткое описание мероприятия")
    @Size(min = 2, message = "Краткое описание мероприятия слишком короткое. Должно быть минимум 2 символа")
    private String annotation;

    @NotNull(message = "ID категории должно быть обязательно указано")
    @Positive(message = "ID категории не может быть отрицательным")
    @Min(value = 1, message = "ID категории не может быть меньше 1")
    private Integer category;

    @NotNull(message = "Описание мероприятия должно быть заполнено")
    @NotBlank(message = "Описание мероприятия заполнено не корректно")
    //@Size(min = 20, max = 3000, message = "Описание мероприятия должно состоять минимум из 20 символов и максимум из 3000")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "Укажите дату и время начала мероприятия")
    @Future(message = "Начало мероприятия может быть только в будущем")
    private LocalDateTime eventDate;

    @NotNull(message = "Место проведения мероприятия должно быть указано")
    private Location location;

    @NotNull(message = "Сообщите, мероприятие платное или нет")
    private Boolean paid;

    @NotNull(message = "Сообщите, есть ли ограничение на количество участников. Если нет, укажите \"0\"")
    @PositiveOrZero(message = "Ограничение на количество участников не может быть отрицательным")
    private Integer participantLimit;

    @NotNull(message = "Укажите нужно ли ручное рассмотрение заявки")
    private Boolean requestModeration;

    @NotNull(message = "Укажите название мероприятия")
    @NotBlank(message = "Не корректное название мероприятия")
    @Size(min = 2, max = 100, message = "Название мероприятия должно состоять минимум из 2 символов и максимум из 100")
    private String title;
}
