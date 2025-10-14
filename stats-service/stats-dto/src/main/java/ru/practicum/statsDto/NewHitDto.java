package ru.practicum.statsDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

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