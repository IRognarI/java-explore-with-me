package ru.practicum.statsServer.exceptions;

public class IsBadRequestException extends RuntimeException {
    public IsBadRequestException(String message) {
        super(message);
    }
}
