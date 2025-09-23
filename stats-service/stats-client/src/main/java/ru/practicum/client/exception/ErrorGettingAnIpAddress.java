package ru.practicum.client.exception;

public class ErrorGettingAnIpAddress extends RuntimeException {
    public ErrorGettingAnIpAddress(String message) {
        super(message);
    }
}
