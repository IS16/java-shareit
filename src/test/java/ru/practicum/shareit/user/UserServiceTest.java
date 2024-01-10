package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.service.MapperService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.UserNotFound;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {
    private final MapperService mapperService;
    private final UserService userService;
    private User user = new User(1L, "Admin", "admin@shareit.ru");

    @Test
    void shouldReturnUser() {
        UserDto userDto = userService.createUser(user);

        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        UserNotFound exception = assertThrows(
                UserNotFound.class,
                () -> userService.getUserById(30L)
        );

        assertEquals("Пользователь с данным id не найден", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUpdateUserNotFound() {
        UserNotFound exception = assertThrows(
                UserNotFound.class,
                () -> userService.updateUser(100L, user)
        );

        assertEquals("Пользователь с данным id не найден", exception.getMessage());
    }

    @Test
    void shouldUpdateUser() {
        UserDto userDto = userService.createUser(user);
        userDto.setName("Test");
        userDto.setEmail("test@shareit.ru");
        userService.updateUser(userDto.getId(), mapperService.toUser(userDto));

        UserDto updatedUser = userService.getUserById(userDto.getId());
        assertEquals(updatedUser.getName(), "Test");
        assertEquals(updatedUser.getEmail(), "test@shareit.ru");
    }

    @Test
    void shouldDeleteUser() {
        UserDto userDto = userService.createUser(user);

        int size = userService.getAllUsers().size();

        userService.deleteUser(userDto.getId());
        assertEquals(size - 1, userService.getAllUsers().size());
    }
}
