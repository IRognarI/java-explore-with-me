package ru.practicum.server.exception;

public class LinksNotFoundException extends RuntimeException {
    public LinksNotFoundException(String message) {
        super(message);
    }
}
