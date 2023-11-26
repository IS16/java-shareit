package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.baseExceptions.ForbiddenError;
import ru.practicum.shareit.error.baseExceptions.ValidationError;
import ru.practicum.shareit.item.exceptions.ItemNotFound;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserService userService;

    @Override
    public Item createItem(Long userId, Item item) {
        validateItem(item);
        validateUser(userId);

        return repository.create(userId, item);
    }

    @Override
    public Item getItemById(Long id) {
        Optional<Item> item = repository.findById(id);
        if (item.isEmpty()) {
            throw new ItemNotFound("Вещь с данным id не найдена");
        }

        return item.get();
    }

    @Override
    public Item updateItem(Long id, Long userId, Item item) {
        if (repository.findById(id).isEmpty()) {
            throw new ItemNotFound("Вещь с данным id не найдена");
        }

        Item foundItem = repository.findById(id).get();

        validateUser(userId);

        if (!userId.equals(foundItem.getOwner())) {
            throw new ForbiddenError("Текущий пользователь не может редактировать данную вещь");
        }

        return repository.update(id, item);
    }

    @Override
    public List<Item> getAllItems(Long userId) {
        validateUser(userId);

        return repository.findByOwnerId(userId);
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        return repository.findByText(text);
    }

    @Override
    public void deleteItem(Long id, Long userId) {
        if (repository.findById(id).isEmpty()) {
            throw new ItemNotFound("Вещь с данным id не найдена");
        }

        Item foundItem = repository.findById(id).get();

        validateUser(userId);

        if (!userId.equals(foundItem.getOwner())) {
            throw new ForbiddenError("Текущий пользователь не может удалить данную вещь");
        }

        repository.deleteItem(id);
        return;
    }

    private void validateUser(Long userId) {
        if (userId == null) {
            throw new ValidationError("Не указан id пользователя");
        }

        userService.getUserById(userId);
    }

    private void validateItem(Item item) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationError("Название вещи не может быть пустым");
        }

        if (item.getAvailable() == null) {
            throw new ValidationError("Доступность вещи не может быть пустой");
        }

        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationError("Описание вещи не может быть пустым");
        }
    }
}
