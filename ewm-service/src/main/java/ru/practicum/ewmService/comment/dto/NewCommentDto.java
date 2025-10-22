package ru.practicum.ewmService.comment.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/**
 * Объект передачи данных (DTO) для создания нового комментария.
 * Этот класс инкапсулирует данные, необходимые для создания нового комментария,
 * включая текст комментария и необязательную оценку.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewCommentDto {
    @NotNull
    @NotBlank
    @Length(min = 3, max = 1000)
    private String text;

    @Min(1)
    @Max(5)
    private Integer rate;
}