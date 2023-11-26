package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public Item add(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody Item item) {
        return itemService.createItem(userId, item);
    }

    @GetMapping("/{id}")
    public Item getById(@PathVariable Long id) {
        return itemService.getItemById(id);
    }

    @PatchMapping("/{id}")
    public Item update(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody Item item) {
        return itemService.updateItem(id, userId, item);
    }

    @GetMapping
    public List<Item> getAllItems(@RequestHeader("X-Sharer-User-Id") Long id) {
        return itemService.getAllItems(id);
    }

    @GetMapping("/search")
    public List<Item> searchItems(@RequestParam(defaultValue = "") String text) {
        return itemService.searchItems(text);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemService.deleteItem(id, userId);
        return;
    }
}
