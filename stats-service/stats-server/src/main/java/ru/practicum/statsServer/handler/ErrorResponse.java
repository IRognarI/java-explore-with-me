package ru.practicum.statsServer.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private final String status;
    private final String message;

    public ErrorResponse(HttpStatus status, Exception ex) {

        this.status = status.name();
        this.message = ex.getMessage();
    }
}