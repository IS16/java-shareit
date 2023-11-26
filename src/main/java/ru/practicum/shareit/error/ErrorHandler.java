package ru.practicum.shareit.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.error.baseExceptions.ForbiddenError;
import ru.practicum.shareit.user.exceptions.UserAlreadyExists;
import ru.practicum.shareit.user.exceptions.UserNotFound;
import ru.practicum.shareit.error.baseExceptions.ValidationError;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({UserAlreadyExists.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyExistsException(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({UserNotFound.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({ValidationError.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({ForbiddenError.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleNotAllowedException(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }
}
