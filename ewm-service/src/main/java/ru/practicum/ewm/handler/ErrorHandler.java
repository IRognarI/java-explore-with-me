package ru.practicum.ewm.handler;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.exception.ArrayLinksIsEmptyException;
import ru.practicum.ewm.exception.UserDuplicatedException;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.exception.ValidationException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
        return new ErrorResponse("CONFLICT", "Ошибка валидации", e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleValidationException(final ValidationException e) {
        return new ErrorResponse("CONFLICT", "Ошибка валидации", e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleArrayLinksIsEmptyException(final ArrayLinksIsEmptyException e) {
        return new ErrorResponse("BAD_REQUEST", "Не корректный запрос", e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(final UserNotFoundException e) {
        return new ErrorResponse("NOT_FOUND", "Ошибка поиска", e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleUserDuplicatedException(final UserDuplicatedException e) {
        return new ErrorResponse("FORBIDDEN", "Ошибка во время регистрации", e.getMessage(), LocalDateTime.now());
    }
}
