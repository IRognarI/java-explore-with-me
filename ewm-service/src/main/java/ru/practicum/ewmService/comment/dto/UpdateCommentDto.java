package ru.practicum.ewmService.comment.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/**
 * Data Transfer Object (DTO) для обновления комментария.
 * Содержит поля, которые могут быть обновлены для комментария, включая текст и рейтинг.
 * Текст не должен быть пустым и должен содержать от 3 до 1000 символов.
 * Рейтинг должен быть в диапазоне от 1 до 5.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCommentDto {
    @NotBlank
    @Length(min = 3, max = 1000)
    private String text;

    @Min(1)
    @Max(5)
    private Integer rate;
}