package ru.practicum.ewmService.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {

    @NotNull
    @NotBlank
    @Length(min = 1, max = 50)
    private String title;
    private Boolean pinned = false;
    List<Long> events;
}