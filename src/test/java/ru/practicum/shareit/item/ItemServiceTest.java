package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.error.baseExceptions.ForbiddenException;
import ru.practicum.shareit.error.baseExceptions.ValidationError;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.ItemNotFound;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    @Test
    void shouldCreateItem() {
        User user = new User(1L, "admin", "admin@shareit.ru");

        Item item1 = new Item(
                3L,
                "Item1",
                "Description",
                true,
                null,
                null
        );

        UserDto newUserDto = userService.createUser(user);
        ItemDto newItemDto = itemService.createItem(newUserDto.getId(), item1);

        ItemDto createdItem = itemService.getItemById(newItemDto.getId(), newUserDto.getId());
        assertEquals(createdItem.getDescription(), item1.getDescription());
    }

    @Test
    void shouldThrowExceptionWhenItemNotFound() {
        User user = new User(1L, "admin", "admin@shareit.ru");

        Item item1 = new Item(
                3L,
                "Item1",
                "Description",
                true,
                null,
                null
        );

        UserDto newUserDto = userService.createUser(user);

        ItemNotFound exception = assertThrows(
                ItemNotFound.class,
                () -> itemService.getItemById(-1L, newUserDto.getId())
        );

        assertEquals("Вещь с данным id не найдена", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDeleteItemByUserNotOwner() {
        User user = new User(3L, "admin", "admin@shareit.ru");
        User user1 = new User(4L, "test", "test@shareit.ru");

        Item item1 = new Item(
                3L,
                "Item1",
                "Description",
                true,
                null,
                null
        );

        UserDto newUserDto = userService.createUser(user);
        UserDto newUserDto1 = userService.createUser(user1);

        ItemDto newItemDto = itemService.createItem(newUserDto.getId(), item1);

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> itemService.deleteItem(newItemDto.getId(), newUserDto1.getId())
        );

        assertEquals("Текущий пользователь не может удалить данную вещь", exception.getMessage());
    }

    @Test
    void shouldReturnItemsBySearchSizeNotNull() {
        User user = new User(3L, "admin", "admin@shareit.ru");
        UserDto newUserDto = userService.createUser(user);

        Item item1 = new Item(
                3L,
                "Item1",
                "Description",
                true,
                null,
                null
        );

        Item item2 = new Item(
                4L,
                "Весь",
                "Des",
                true,
                null,
                null
        );

        itemService.createItem(newUserDto.getId(), item1);
        itemService.createItem(newUserDto.getId(), item2);

        List<ItemDto> itemsList = itemService.searchItems("item", 0, 4);
        assertEquals(1, itemsList.size());
    }

    @Test
    void shouldReturnItemsBySearchSizeNull() {
        User user = new User(3L, "admin", "admin@shareit.ru");
        UserDto newUserDto = userService.createUser(user);

        Item item1 = new Item(
                3L,
                "Item1",
                "Description",
                true,
                null,
                null
        );

        Item item2 = new Item(
                4L,
                "Весь",
                "Des",
                true,
                null,
                null
        );

        itemService.createItem(newUserDto.getId(), item1);
        itemService.createItem(newUserDto.getId(), item2);

        List<ItemDto> itemsList = itemService.searchItems("item", 0, null);
        assertEquals(1, itemsList.size());
    }

    @Test
    void shouldCreateComment() {
        User user = new User(3L, "admin", "admin@shareit.ru");
        UserDto newUserDto = userService.createUser(user);

        User user1 = new User(5L, "test", "test@shareit.ru");
        UserDto newUserDto1 = userService.createUser(user1);

        Item item1 = new Item(
                3L,
                "Item1",
                "Description",
                true,
                null,
                null
        );
        ItemDto newItemDto = itemService.createItem(newUserDto.getId(), item1);

        BookingInputDto bookingInputDto = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2024, 1, 1, 0, 0),
                LocalDateTime.of(2024, 1, 2, 0, 0)
        );
        BookingDto bookingDto = bookingService.create(newUserDto1.getId(), bookingInputDto);
        bookingService.update(newUserDto.getId(), bookingDto.getId(), true);

        CommentDto commentDto = new CommentDto(
                1L,
                "Тест",
                item1,
                "Author",
                LocalDateTime.now()
        );
        itemService.createComment(commentDto, newItemDto.getId(), newUserDto1.getId());

        assertEquals(1, itemService.getCommentsByItemId(newItemDto.getId()).size());
    }

    @Test
    void shouldThrowExceptionWhenUserNotBooker() {
        User user = new User(3L, "admin", "admin@shareit.ru");
        UserDto newUserDto = userService.createUser(user);

        User user1 = new User(5L, "test", "test@shareit.ru");
        UserDto newUserDto1 = userService.createUser(user1);

        Item item1 = new Item(
                3L,
                "Item1",
                "Description",
                true,
                null,
                null
        );
        ItemDto newItemDto = itemService.createItem(newUserDto.getId(), item1);

        BookingInputDto bookingInputDto = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2024, 1, 1, 0, 0),
                LocalDateTime.of(2024, 1, 2, 0, 0)
        );
        BookingDto bookingDto = bookingService.create(newUserDto1.getId(), bookingInputDto);
        bookingService.update(newUserDto.getId(), bookingDto.getId(), true);

        CommentDto commentDto = new CommentDto(
                1L,
                "Тест",
                item1,
                "Author",
                LocalDateTime.now()
        );

        ValidationError exception = assertThrows(
                ValidationError.class,
                () -> itemService.createComment(commentDto, newItemDto.getId(), newUserDto.getId())
        );

        assertEquals("Вещь не была забронирована данным пользователем", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUpdateItemNotFound() {
        User user = new User(3L, "admin", "admin@shareit.ru");
        UserDto newUserDto = userService.createUser(user);

        Item item1 = new Item(
                3L,
                "Item1",
                "Description",
                true,
                null,
                null
        );
        ItemDto newItemDto = itemService.createItem(newUserDto.getId(), item1);

        ItemNotFound exception = assertThrows(
                ItemNotFound.class,
                () -> itemService.updateItem(10L, user.getId(), item1)
        );

        assertEquals("Вещь с данным id не найдена", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUpdateWithUserNotOwner() {
        User user = new User(3L, "admin", "admin@shareit.ru");
        UserDto newUserDto = userService.createUser(user);

        Item item1 = new Item(
                3L,
                "Item1",
                "Description",
                true,
                null,
                null
        );
        ItemDto newItemDto = itemService.createItem(newUserDto.getId(), item1);

        UserDto newUserDto1 = userService.createUser(new User(4L, "support", "support@shareit.ru"));

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> itemService.updateItem(newItemDto.getId(), newUserDto1.getId(), item1)
        );

        assertEquals("Текущий пользователь не может редактировать данную вещь", exception.getMessage());
    }

    @Test
    void shouldUpdateItem() {
        User user = new User(3L, "admin", "admin@shareit.ru");
        UserDto newUserDto = userService.createUser(user);

        Item item1 = new Item(
                3L,
                "Item1",
                "Description",
                true,
                null,
                null
        );
        ItemDto newItemDto = itemService.createItem(newUserDto.getId(), item1);

        Item updatedItem = new Item(
                newItemDto.getId(),
                "Новое название",
                null,
                null,
                null,
                null
        );

        itemService.updateItem(updatedItem.getId(), newUserDto.getId(), updatedItem);

        updatedItem = new Item(
                newItemDto.getId(),
                null,
                "New desc",
                null,
                null,
                null
        );

        itemService.updateItem(updatedItem.getId(), newUserDto.getId(), updatedItem);
    }

    @Test
    void shouldThrowExceptionWhenUpdateNameBlank() {
        User user = new User(3L, "admin", "admin@shareit.ru");
        UserDto newUserDto = userService.createUser(user);

        Item item1 = new Item(
                3L,
                "Item1",
                "Description",
                true,
                null,
                null
        );
        ItemDto newItemDto = itemService.createItem(newUserDto.getId(), item1);

        item1.setName(" ");

        ValidationError exception = assertThrows(
                ValidationError.class,
                () -> itemService.updateItem(newItemDto.getId(), newUserDto.getId(), item1)
        );

        assertEquals("Название предмета не может быть пустым", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUpdateDescriptionBlank() {
        User user = new User(3L, "admin", "admin@shareit.ru");
        UserDto newUserDto = userService.createUser(user);

        Item item1 = new Item(
                3L,
                "Item1",
                "Description",
                true,
                null,
                null
        );
        ItemDto newItemDto = itemService.createItem(newUserDto.getId(), item1);

        item1.setDescription(" ");

        ValidationError exception = assertThrows(
                ValidationError.class,
                () -> itemService.updateItem(newItemDto.getId(), newUserDto.getId(), item1)
        );

        assertEquals("Описание предмета не может быть пустым", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDeleteItemNotFound() {
        User user = new User(3L, "admin", "admin@shareit.ru");
        UserDto newUserDto = userService.createUser(user);

        Item item1 = new Item(
                3L,
                "Item1",
                "Description",
                true,
                null,
                null
        );
        ItemDto newItemDto = itemService.createItem(newUserDto.getId(), item1);

        ItemNotFound exception = assertThrows(
                ItemNotFound.class,
                () -> itemService.deleteItem(100L, newUserDto.getId())
        );

        assertEquals("Вещь с данным id не найдена", exception.getMessage());
    }

    @Test
    void shouldReturnAllItemsSizeNull() {
        User user = new User(3L, "admin", "admin@shareit.ru");
        UserDto newUserDto = userService.createUser(user);

        Item item1 = new Item(
                3L,
                "Item1",
                "Description",
                true,
                null,
                null
        );

        Item item2 = new Item(
                4L,
                "Весь",
                "Des",
                true,
                null,
                null
        );

        itemService.createItem(newUserDto.getId(), item1);
        itemService.createItem(newUserDto.getId(), item2);

        assertEquals(2, itemService.getAllItems(newUserDto.getId(), 0, null).size());
    }

    @Test
    void shouldReturnAllItemsSizeNotNull() {
        User user = new User(3L, "admin", "admin@shareit.ru");
        UserDto newUserDto = userService.createUser(user);

        Item item1 = new Item(
                3L,
                "Item1",
                "Description",
                true,
                null,
                null
        );

        Item item2 = new Item(
                4L,
                "Весь",
                "Des",
                true,
                null,
                null
        );

        itemService.createItem(newUserDto.getId(), item1);
        itemService.createItem(newUserDto.getId(), item2);

        assertEquals(1, itemService.getAllItems(newUserDto.getId(), 0, 1).size());
    }
}
