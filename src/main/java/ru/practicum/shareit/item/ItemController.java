package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody Item item) {
        return itemService.createItem(userId, item);
    }

    @GetMapping("/{id}")
    public ItemDto getById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long id) {
        return itemService.getItemById(id, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody Item item) {
        return itemService.updateItem(id, userId, item);
    }

    @GetMapping
    public List<ItemDto> getAllItems(
            @RequestHeader("X-Sharer-User-Id") Long id,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(required = false) Integer size
            ) {
        return itemService.getAllItems(id, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(
            @RequestParam(defaultValue = "") String text,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(required = false) Integer size) {
        return itemService.searchItems(text, from, size);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemService.deleteItem(id, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId, @Valid @RequestBody CommentDto commentDto) {
        return itemService.createComment(commentDto, itemId, userId);
    }
}
