package ru.practicum.client.exception;

public class LinksNotFoundException extends RuntimeException {
    public LinksNotFoundException(String message) {
        super(message);
    }
}
