package ru.practicum.ewm.handler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ErrorResponse {
    private final static DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String status;
    private String reason;
    private String message;
    private LocalDateTime timestamp;

    public ErrorResponse(String status, String reason, String message, LocalDateTime timestamp) {
        this.status = status;
        this.reason = reason;
        this.message = message;
        this.timestamp = timestamp;
    }

    public ErrorResponse() {

    }

    public String getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp.format(format);
    }
}
