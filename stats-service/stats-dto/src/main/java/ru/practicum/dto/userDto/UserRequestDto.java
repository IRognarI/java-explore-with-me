package ru.practicum.dto.userDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRequestDto {

    @NotNull(message = "Имя должно быть указано")
    private String name;

    @Email(message = "Укажите корректный email")
    @NotNull(message = "Email должен быть указан")
    private String email;
}
