package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private Long currentId = 0L;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item create(Long userId, Item item) {
        Long id = getId();
        item.setId(id);
        item.setOwner(userId);
        items.put(id, item);
        return item;
    }

    @Override
    public Optional<Item> findById(Long id) {
        if (items.containsKey(id)) {
            return Optional.of(items.get(id));
        }

        return Optional.empty();
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public List<Item> findByOwnerId(Long userId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findByText(String text) {
        return items.values()
                .stream()
                .filter(Item::getAvailable)
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase()) || item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Long id) {
        items.remove(id);
        return;
    }

    private Long getId() {
        return ++currentId;
    }
}
