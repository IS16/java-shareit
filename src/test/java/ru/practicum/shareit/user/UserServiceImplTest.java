package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.exceptions.UserNotFound;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository mockUserRepository;

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        UserService userService = new UserServiceImpl(mockUserRepository, null);

        when(mockUserRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        UserNotFound exception = Assertions.assertThrows(
                UserNotFound.class,
                () -> userService.getUserById(-1L)
        );

        Assertions.assertEquals("Пользователь с данным id не найден", exception.getMessage());
    }
}
