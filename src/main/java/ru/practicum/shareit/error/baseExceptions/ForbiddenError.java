package ru.practicum.shareit.error.baseExceptions;

public class ForbiddenError extends RuntimeException {
    public ForbiddenError(String message) {
        super(message);
    }
}
