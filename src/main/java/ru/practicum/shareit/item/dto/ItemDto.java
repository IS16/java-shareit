package ru.practicum.shareit.item.dto;

/**
 * TODO Sprint add-controllers.
 */

public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long request;

    public ItemDto(Long id, String name, String description, Boolean available, Long request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = request;
    }
}
