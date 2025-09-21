package ru.practicum.server.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Getter
@Setter
@EqualsAndHashCode(of = {"app", "uri"})
@ToString
public class RequestDto {

    @NotNull(message = "Сервис не может быть пустым")
    private String app;

    @NotNull(message = "URI не может быть пустым")
    private String uri;

    @NotNull(message = "IP не может быть пустым")
    private String ip;

    @NotNull(message = "Дата не может быть пустой")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
