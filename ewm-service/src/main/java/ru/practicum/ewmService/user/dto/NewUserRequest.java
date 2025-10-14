package ru.practicum.ewmService.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {

    @NotNull
    @Length(min = 6, max = 254)
    @Email
    private String email;

    @NotNull
    @NotBlank
    @Length(min = 2, max = 250)
    private String name;
}