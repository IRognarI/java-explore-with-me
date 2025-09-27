package ru.practicum.dto.userDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@ToString
@Getter
public class UserDto {
    @Positive(message = "ID не может быть отрицательным")
    private Long id;

    @Email(message = "Укажите корректный email адрес")
    @NotNull(message = "Email адрес обязателен для заполнения")
    private String email;

    @NotNull(message = "Имя обязательно для заполнения")
    @NotEmpty(message = "Имя не может быть пустым")
    @NotBlank(message = "Укажите корректное имя")
    private String name;
}
