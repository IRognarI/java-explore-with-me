package ru.practicum.statsServer.handler;

import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.statsServer.exceptions.IsBadRequestException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {


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