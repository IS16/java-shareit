package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.exceptions.BookingNotFound;
import ru.practicum.shareit.service.ValidationService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @Mock
    BookingRepository mockBookingRepository;

    @Mock
    ValidationService validationService;

    private UserDto user = new UserDto(
            1L,
            "Admin",
        "admin@shareit.ru"
    );

    @Test
    void shouldThrowExceptionBookingNotFound() {
        BookingService bookingService = new BookingServiceImpl(validationService, null, mockBookingRepository);

        when(validationService.getUserById(any(Long.class)))
                .thenReturn(user);

        when(mockBookingRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        final BookingNotFound exception = Assertions.assertThrows(
                BookingNotFound.class,
                () -> bookingService.getBookingById(1L, 1L));

        Assertions.assertEquals("Бронирование с id = 1 не найдено", exception.getMessage());
    }
}
