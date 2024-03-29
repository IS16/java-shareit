package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, Item item);

    ItemDto getItemById(Long id, Long userId);

    ItemDto updateItem(Long id, Long userId, Item item);

    List<ItemDto> getAllItems(Long userId, Integer from, Integer size);

    List<ItemDto> searchItems(String text, Integer from, Integer size);

    void deleteItem(Long id, Long userId);

    CommentDto createComment(CommentDto commentDto, Long itemId, Long userId);

    List<CommentDto> getCommentsByItemId(Long itemId);

    List<ItemDto> getItemsByRequestId(Long requestId);
}
