package ru.practicum.server.exception;

public class ErrorGettingAnIpAddress extends RuntimeException {
    public ErrorGettingAnIpAddress(String message) {
        super(message);
    }
}
