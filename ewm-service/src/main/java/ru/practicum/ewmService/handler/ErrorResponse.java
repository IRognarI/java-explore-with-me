package ru.practicum.ewmService.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import ru.practicum.ewmService.dateTimeFormatter.Formatter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private final String status;
    private final String reason;
    private final String message;
    private final String timestamp;

    public ErrorResponse(HttpStatus status, Exception ex) {

        this.status = status.name();
        this.reason = status.getReasonPhrase();
        this.message = ex.getMessage();
        this.timestamp = LocalDateTime.now().format(Formatter.FORMATTER);
    }
}