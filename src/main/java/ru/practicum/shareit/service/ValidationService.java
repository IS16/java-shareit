package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

@Service
@RequiredArgsConstructor
public class ValidationService {
    private final UserService userService;
    private final ItemService itemService;

    public UserDto getUserById(Long id) {
        return userService.getUserById(id);
    }

    public ItemDto getItemById(Long id, Long userId) {
        return itemService.getItemById(id, userId);
    }
}
