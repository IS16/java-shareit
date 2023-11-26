package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createItem(Long userId, Item item);
    Item getItemById(Long id);
    Item updateItem(Long id, Long userId, Item item);
    List<Item> getAllItems(Long userId);
    List<Item> searchItems(String text);
    void deleteItem(Long id, Long userId);
}
