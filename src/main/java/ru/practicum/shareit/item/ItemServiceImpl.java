package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.baseExceptions.ForbiddenException;
import ru.practicum.shareit.error.baseExceptions.ValidationError;
import ru.practicum.shareit.item.exceptions.ItemNotFound;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserService userService;

    @Override
    public Item createItem(Long userId, Item item) {
        validateUser(userId);
        log.info("Создан новый предмет: " + item);
        return repository.create(userId, item);
    }

    @Override
    public Item getItemById(Long id) {
        Optional<Item> item = repository.findById(id);
        if (item.isEmpty()) {
            log.info(String.format("Предмет с id = %d не найден", id));
            throw new ItemNotFound("Вещь с данным id не найдена");
        }

        log.info("Получена информация о предмете с id = " + id);
        return item.get();
    }

    @Override
    public Item updateItem(Long id, Long userId, Item item) {
        if (repository.findById(id).isEmpty()) {
            log.info(String.format("Предмет с id = %d не найден", id));
            throw new ItemNotFound("Вещь с данным id не найдена");
        }

        Item foundItem = repository.findById(id).get();

        if (!userId.equals(foundItem.getOwner())) {
            log.info("Попытка изменить вещь другим пользователем");
            throw new ForbiddenException("Текущий пользователь не может редактировать данную вещь");
        }

        validateUser(userId);

        item.setId(id);
        item.setOwner(userId);

        if (item.getName() == null) {
            item.setName(foundItem.getName());
        } else if (item.getName().isBlank()) {
            log.info("Обновление предмета не прошло валидацию названия (" + item + ")");
            throw new ValidationError("Название предмета не может быть пустым");
        }

        if (item.getDescription() == null) {
            item.setDescription(foundItem.getDescription());
        } else if (item.getDescription().isBlank()) {
            log.info("Обновление предмета не прошло валидацию описания (" + item + ")");
            throw new ValidationError("Описание предмета не может быть пустым");
        }

        if (item.getAvailable() == null) {
            item.setAvailable(foundItem.getAvailable());
        }

        if (item.getRequest() == null) {
            item.setRequest(foundItem.getRequest());
        }

        log.info("Обновлён предмет: " + item);
        return repository.update(item);
    }

    @Override
    public List<Item> getAllItems(Long userId) {
        validateUser(userId);

        log.info("Получен список всех предметов");
        return repository.findByOwnerId(userId);
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        log.info(String.format("Поиск предметов по подстроке \"%s\"", text));
        return repository.findByText(text);
    }

    @Override
    public void deleteItem(Long id, Long userId) {
        if (repository.findById(id).isEmpty()) {
            log.info(String.format("Предмет с id = %d не найден", id));
            throw new ItemNotFound("Вещь с данным id не найдена");
        }

        Item foundItem = repository.findById(id).get();

        log.info("Удалён предмет с id = " + id);
        validateUser(userId);

        if (!userId.equals(foundItem.getOwner())) {
            throw new ForbiddenException("Текущий пользователь не может удалить данную вещь");
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
}
