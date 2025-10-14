package ru.practicum.ewmService.exceptions;

public class IntegrityException extends RuntimeException {
    public IntegrityException(String message) {
        super(message);
    }
}
