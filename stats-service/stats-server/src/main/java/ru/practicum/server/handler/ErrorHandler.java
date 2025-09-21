package ru.practicum.server.handler;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.server.exception.ErrorGettingAnIpAddress;
import ru.practicum.server.exception.ValidationException;

import java.util.Collections;
import java.util.Map;

@SpringBootApplication
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public Map<String, String> handleValidationException(final ValidationException e) {
        return Collections.singletonMap("Ошибка валидации", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleErrorGettingAnIpAddress(final ErrorGettingAnIpAddress e) {
        return Collections.singletonMap("IP не найден", e.getMessage());
    }
}
