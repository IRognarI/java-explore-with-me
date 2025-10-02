package ru.practicum.ewm.handler;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.ewm.exception.ArrayLinksIsEmptyException;
import ru.practicum.ewm.exception.DateTimeCheckException;
import ru.practicum.ewm.exception.ObjectDuplicatedException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.exception.StateValidationException;
import ru.practicum.ewm.exception.ValidationException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
        return new ErrorResponse("BAD_REQUEST", "Ошибка валидации", e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        return new ErrorResponse("BAD_REQUEST", "Ошибка валидации", e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleArrayLinksIsEmptyException(final ArrayLinksIsEmptyException e) {
        return new ErrorResponse("BAD_REQUEST", "Не корректный запрос", e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(final ObjectNotFoundException e) {
        return new ErrorResponse("NOT_FOUND", "Ошибка поиска", e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserDuplicatedException(final ObjectDuplicatedException e) {
        return new ErrorResponse("CONFLICT", "Ошибка регистрации", e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDateTimeCheckException(final DateTimeCheckException e) {
        return new ErrorResponse("CONFLICT", "Дата начала мероприятия не прошла проверку", e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {
        return new ErrorResponse("BAD_REQUEST", "Не корректный запрос", e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleStateValidationException(final StateValidationException e) {
        return new ErrorResponse("CONFLICT", "Ошибка установки статуса события", e.getMessage(), LocalDateTime.now());
    }
}
