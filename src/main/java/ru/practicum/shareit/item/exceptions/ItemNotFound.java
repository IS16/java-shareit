package ru.practicum.shareit.item.exceptions;

public class ItemNotFound extends RuntimeException {
    public ItemNotFound(String message) {
        super(message);
    }
}
