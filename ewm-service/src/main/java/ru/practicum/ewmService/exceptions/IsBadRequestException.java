package ru.practicum.ewmService.exceptions;

public class IsBadRequestException extends RuntimeException {
    public IsBadRequestException(String message) {
        super(message);
    }
}
