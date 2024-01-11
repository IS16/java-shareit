package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exceptions.ItemRequestNotFound;
import ru.practicum.shareit.service.MapperService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.UserNotFound;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceTest {
    private final MapperService mapperService;

    private final ItemRequestService itemRequestService;
    private final UserService userService;

    private UserDto userDto1 = new UserDto(1L, "admin", "admin@shareit.ru");
    private UserDto userDto2 = new UserDto(2L, "test", "test@shareit.ru");

    private ItemRequestDto itemRequestDto = new ItemRequestDto(
            1L,
            "Описание",
            userDto1,
            LocalDateTime.of(2024, 2, 1, 0, 0),
            null
            );

    @Test
    void shouldThrowExceptionWhenCreateItemRequestWithWrondUserId() {
        UserNotFound exp = assertThrows(
                UserNotFound.class,
                () -> itemRequestService.create(itemRequestDto, -1L)
        );

        assertEquals("Пользователь с данным id не найден", exp.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenGetItemRequestWithWrongId() {
        UserDto userDto = userService.createUser(userDto1);

        ItemRequestNotFound exp = assertThrows(
                ItemRequestNotFound.class,
                () -> itemRequestService.getItemRequestById(-2L, userDto.getId())
        );

        assertEquals("Запрос с id = -2 не найден", exp.getMessage());
    }

    @Test
    void shouldReturnOwnItemRequests() {
        UserDto userDto = userService.createUser(userDto1);

        itemRequestService.create(itemRequestDto, userDto.getId());

        ItemRequestDto itemRequestDto1 = new ItemRequestDto(
                3L,
                "Тест",
                userDto,
                LocalDateTime.of(2024, 2, 1, 0, 0),
                null
        );
        itemRequestService.create(itemRequestDto1, userDto.getId());


        assertEquals(2, itemRequestService.getOwnItemRequests(userDto.getId()).size());
    }

    @Test
    void shouldReturnAllItemRequestsWithSizeNull() {
        UserDto user = userService.createUser(userDto1);
        UserDto user1 = userService.createUser(userDto2);

        ItemRequestDto itemRequestDto1 = new ItemRequestDto(
                4L,
                "Тт",
                user1,
                LocalDateTime.of(2025, 3, 1, 0, 0),
                null
        );

        itemRequestService.create(itemRequestDto1, user1.getId());

        assertEquals(1, itemRequestService.getAllItemRequests(user.getId(), 0, null).size());
    }

    @Test
    void shouldReturnAllItemRequestsWithSizeNotNull() {
        UserDto user = userService.createUser(userDto1);
        UserDto user1 = userService.createUser(userDto2);

        ItemRequestDto itemRequestDto1 = new ItemRequestDto(
                4L,
                "Тт",
                user1,
                LocalDateTime.of(2025, 3, 1, 0, 0),
                null
        );

        itemRequestService.create(itemRequestDto1, user1.getId());

        ItemRequestDto itemRequestDto2 = new ItemRequestDto(
                5L,
                "Тdsdsт",
                user1,
                LocalDateTime.of(2025, 3, 1, 0, 0),
                null
        );

        itemRequestService.create(itemRequestDto2, user1.getId());

        assertEquals(1, itemRequestService.getAllItemRequests(user.getId(), 0, 1).size());
    }
}
