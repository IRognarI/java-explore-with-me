package ru.practicum.server.exception;

public class ErrorSavingStatistics extends RuntimeException {
    public ErrorSavingStatistics(String message) {
        super(message);
    }
}
