package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item create(Long userId, Item item);

    Optional<Item> findById(Long id);

    Item update(Item item);

    List<Item> findByOwnerId(Long userId);

    List<Item> findByText(String text);

    void deleteItem(Long id);
}
