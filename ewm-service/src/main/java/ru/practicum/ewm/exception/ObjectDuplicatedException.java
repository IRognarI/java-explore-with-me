package ru.practicum.ewm.exception;

public class ObjectDuplicatedException extends RuntimeException {
    public ObjectDuplicatedException(String message) {
        super(message);
    }
}
