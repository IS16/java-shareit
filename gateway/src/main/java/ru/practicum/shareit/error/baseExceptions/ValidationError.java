package ru.practicum.shareit.error.baseExceptions;

public class ValidationError extends RuntimeException {
    public ValidationError(String message) {
        super(message);
    }
}
