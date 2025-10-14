package ru.practicum.ewmService.handler;

import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewmService.exceptions.IntegrityException;
import ru.practicum.ewmService.exceptions.IsBadRequestException;
import ru.practicum.ewmService.exceptions.NotFoundException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {


    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(IntegrityException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflict(final IntegrityException e) {
        log.warn("Error", e);
        return new ErrorResponse(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler({ServletException.class, BindException.class, IsBadRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleServletException(final Exception e) {
        log.warn("Error", e);
        return new ErrorResponse(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Exception e) {
        log.warn("Error", e);
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }
}