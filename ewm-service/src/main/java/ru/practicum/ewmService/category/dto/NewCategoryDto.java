package ru.practicum.ewmService.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/**
 * Объект передачи данных (DTO) для создания новой категории.
 * Этот класс используется для передачи данных категории от клиента к серверу при создании новой категории.
 * Он включает ограничения валидации, чтобы убедиться, что имя не равно null, не пустое и имеет допустимую длину.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCategoryDto {
    @NotNull
    @NotBlank
    @Length(min = 1, max = 50)
    private String name;
}