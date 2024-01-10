package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.exceptions.ItemRequestNotFound;
import ru.practicum.shareit.service.ValidationService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
public class RequestServiceImplTest {
    @Mock
    private ItemRequestRepository mockItemRequestRepository;

    @Mock
    private ValidationService validationService;

    private UserDto user = new UserDto(
            1L,
            "Admin",
            "admin@shareit.ru"
    );

    @Test
    void shouldThrowExceptionItemRequestNotFound() {
        ItemRequestService itemRequestService = new ItemRequestServiceImpl(null, validationService, mockItemRequestRepository, null);

        when(validationService.getUserById(any(Long.class))).thenReturn(user);

        when(mockItemRequestRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        final ItemRequestNotFound exception = Assertions.assertThrows(
                ItemRequestNotFound.class,
                () -> itemRequestService.getItemRequestById(1L, 1L)
        );

        Assertions.assertEquals("Запрос с id = 1 не найден", exception.getMessage());
    }
}
