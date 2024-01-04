package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.exceptions.BookingNotFound;
import ru.practicum.shareit.error.baseExceptions.ValidationError;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.service.MapperService;
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
public class BookingServiceTest {
    private final MapperService mapperService;

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    private User user1 = new User(1L, "Admin", "admin@shareit.ru");

    private UserDto userDto1 = new UserDto(
            1L,
            "Admin",
            "admin@shareit.ru"
    );

    private User user2 = new User(2L, "Test", "test@shareit.ru");
    private User user3 = new User(3L, "support", "support@shareit.ru");

    private ItemDto item1 = new ItemDto(
            11L, "Вещь1", "Описание", true, userDto1, null, null, null, null
    );

    @Test
    void shouldThrowExceptionWhenCreateNotAvailableItem() {
        UserDto newUserDto = userService.createUser(user1);
        UserDto newUserDto1 = userService.createUser(new User(20L, "support", "support@shareit.ru"));
        ItemDto newItemDto = itemService.createItem(newUserDto.getId(), mapperService.toItem(item1));

        item1.setAvailable(false);
        itemService.updateItem(newItemDto.getId(), newUserDto.getId(), mapperService.toItem(item1));

        BookingInputDto bookingInputDto1 = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2023, 2, 1, 0, 0),
                LocalDateTime.of(2023, 2, 2, 0, 0)
        );

        ValidationError exception = assertThrows(
                ValidationError.class,
                () -> bookingService.create(newUserDto1.getId(), bookingInputDto1)
        );

        assertEquals("Данная вещь недоступна для бронирования", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCreateFinishDateBeforeStartDate() {
        UserDto newUserDto = userService.createUser(user1);
        UserDto newUserDto1 = userService.createUser(new User(20L, "support", "support@shareit.ru"));
        ItemDto newItemDto = itemService.createItem(newUserDto.getId(), mapperService.toItem(item1));

        BookingInputDto bookingInputDto1 = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2023, 2, 2, 0, 0),
                LocalDateTime.of(2023, 2, 1, 0, 0)
        );

        ValidationError exception = assertThrows(
                ValidationError.class,
                () -> bookingService.create(newUserDto1.getId(), bookingInputDto1)
        );

        assertEquals("Дата окончания бранирования не может быть раньше, чем дата начала", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCreateBookingByItemOwner() {
        UserDto newUserDto = userService.createUser(user1);
        ItemDto newItemDto = itemService.createItem(newUserDto.getId(), mapperService.toItem(item1));

        BookingInputDto bookingInputDto1 = new BookingInputDto(
                newItemDto.getId(),
                LocalDateTime.of(2023, 2, 1, 0, 0),
                LocalDateTime.of(2023, 2, 2, 0, 0)
        );

        BookingNotFound exp = assertThrows(
                BookingNotFound.class,
                () -> bookingService.create(newUserDto.getId(), bookingInputDto1));

        assertEquals("Владелец вещи не может забронировать собственную вещь", exp.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenGetBookingByNotOwnerNotBooker() {
        UserDto userDto1 = userService.createUser(user1);
        UserDto userDto2 = userService.createUser(user2);
        UserDto userDto3 = userService.createUser(new User(1000L, "support", "support@shareit.ru"));

        ItemDto itemDto = itemService.createItem(userDto1.getId(), mapperService.toItem(item1));

        BookingInputDto bookingInputDto1 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2023, 2, 1, 0, 0),
                LocalDateTime.of(2023, 2, 2, 0, 0)
        );

        BookingDto bookingDto = bookingService.create(userDto2.getId(), bookingInputDto1);

        BookingNotFound exp = assertThrows(
                BookingNotFound.class,
                () -> bookingService.getBookingById(userDto3.getId(), bookingDto.getId()));

        assertEquals("Посмотреть данные бронирования могут только владелец вещи и создатель бронирования", exp.getMessage());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsByBookerSizeIsNull() {
        UserDto userDto1 = userService.createUser(user1);
        UserDto userDto2 = userService.createUser(user2);

        ItemDto itemDto = itemService.createItem(userDto1.getId(), mapperService.toItem(item1));

        BookingInputDto bookingInputDto1 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2023, 2, 1, 0, 0),
                LocalDateTime.of(2023, 2, 2, 0, 0)
        );

        BookingDto bookingDto = bookingService.create(userDto2.getId(), bookingInputDto1);

        BookingInputDto bookingInputDto2 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2023, 2, 1, 0, 0),
                LocalDateTime.of(2023, 2, 2, 0, 0)
        );

        BookingDto bookingDto1 = bookingService.create(userDto2.getId(), bookingInputDto2);

        List<BookingDto> bookingDtoList = bookingService.getBookings(userDto2.getId(), "ALL", 0, null);
        assertEquals(2, bookingDtoList.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsByBookerSizeIsNotNull() {
        UserDto userDto1 = userService.createUser(user1);
        UserDto userDto2 = userService.createUser(user2);

        ItemDto itemDto = itemService.createItem(userDto1.getId(), mapperService.toItem(item1));

        BookingInputDto bookingInputDto1 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2023, 2, 1, 0, 0),
                LocalDateTime.of(2023, 2, 2, 0, 0)
        );

        BookingDto bookingDto = bookingService.create(userDto2.getId(), bookingInputDto1);

        BookingInputDto bookingInputDto2 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2023, 2, 1, 0, 0),
                LocalDateTime.of(2023, 2, 2, 0, 0)
        );

        BookingDto bookingDto1 = bookingService.create(userDto2.getId(), bookingInputDto2);

        List<BookingDto> bookingDtoList = bookingService.getBookings(userDto2.getId(), "ALL", 0, 1);
        assertEquals(1, bookingDtoList.size());
    }

    @Test
    public void shouldReturnBookingsWhenGetBookingsByBookerSizeIsNullDifferentStates() {
        UserDto userDto1 = userService.createUser(user1);
        UserDto userDto2 = userService.createUser(user2);

        ItemDto itemDto = itemService.createItem(userDto1.getId(), mapperService.toItem(item1));

        BookingInputDto bookingInputDto1 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2023, 2, 1, 0, 0),
                LocalDateTime.of(2023, 2, 2, 0, 0)
        );

        BookingDto bookingDto = bookingService.create(userDto2.getId(), bookingInputDto1);

        BookingInputDto bookingInputDto2 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2023, 2, 1, 0, 0),
                LocalDateTime.of(2023, 2, 2, 0, 0)
        );

        BookingDto bookingDto1 = bookingService.create(userDto2.getId(), bookingInputDto2);

        List<BookingDto> bookingDtoList = bookingService.getBookings(userDto2.getId(), "REJECTED", 0, null);
        assertEquals(0, bookingDtoList.size());

        bookingDtoList = bookingService.getBookings(userDto2.getId(), "CURRENT", 0, null);
        assertEquals(0, bookingDtoList.size());

        bookingDtoList = bookingService.getBookings(userDto2.getId(), "PAST", 0, null);
        assertEquals(2, bookingDtoList.size());

        bookingDtoList = bookingService.getBookings(userDto2.getId(), "FUTURE", 0, null);
        assertEquals(0, bookingDtoList.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsByOwnerSizeIsNull() {
        UserDto userDto1 = userService.createUser(user1);
        UserDto userDto2 = userService.createUser(user2);

        ItemDto itemDto = itemService.createItem(userDto1.getId(), mapperService.toItem(item1));

        BookingInputDto bookingInputDto1 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2023, 2, 1, 0, 0),
                LocalDateTime.of(2023, 2, 2, 0, 0)
        );

        BookingDto bookingDto = bookingService.create(userDto2.getId(), bookingInputDto1);

        BookingInputDto bookingInputDto2 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2023, 2, 1, 0, 0),
                LocalDateTime.of(2023, 2, 2, 0, 0)
        );

        BookingDto bookingDto1 = bookingService.create(userDto2.getId(), bookingInputDto2);

        List<BookingDto> bookingDtoList = bookingService.getOwnerBookings(userDto1.getId(), "ALL", 0, null);
        assertEquals(2, bookingDtoList.size());
    }

    @Test
    void shouldReturnBookingsWhenGetBookingsByOwnerSizeIsNotNull() {
        UserDto userDto1 = userService.createUser(user1);
        UserDto userDto2 = userService.createUser(user2);

        ItemDto itemDto = itemService.createItem(userDto1.getId(), mapperService.toItem(item1));

        BookingInputDto bookingInputDto1 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2023, 2, 1, 0, 0),
                LocalDateTime.of(2023, 2, 2, 0, 0)
        );

        BookingDto bookingDto = bookingService.create(userDto2.getId(), bookingInputDto1);

        BookingInputDto bookingInputDto2 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2023, 2, 1, 0, 0),
                LocalDateTime.of(2023, 2, 2, 0, 0)
        );

        BookingDto bookingDto1 = bookingService.create(userDto2.getId(), bookingInputDto2);

        List<BookingDto> bookingDtoList = bookingService.getOwnerBookings(userDto1.getId(), "ALL", 0, 1);
        assertEquals(1, bookingDtoList.size());
    }

    @Test
    public void shouldReturnBookingsWhenGetBookingsByOwnerSizeIsNullDifferentStates() {
        UserDto userDto1 = userService.createUser(user1);
        UserDto userDto2 = userService.createUser(user2);

        ItemDto itemDto = itemService.createItem(userDto1.getId(), mapperService.toItem(item1));

        BookingInputDto bookingInputDto1 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2023, 2, 1, 0, 0),
                LocalDateTime.of(2023, 2, 2, 0, 0)
        );

        BookingDto bookingDto = bookingService.create(userDto2.getId(), bookingInputDto1);

        BookingInputDto bookingInputDto2 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2023, 2, 1, 0, 0),
                LocalDateTime.of(2023, 2, 2, 0, 0)
        );

        BookingDto bookingDto1 = bookingService.create(userDto2.getId(), bookingInputDto2);

        List<BookingDto> bookingDtoList = bookingService.getOwnerBookings(userDto1.getId(), "REJECTED", 0, null);
        assertEquals(0, bookingDtoList.size());

        bookingDtoList = bookingService.getOwnerBookings(userDto1.getId(), "CURRENT", 0, null);
        assertEquals(0, bookingDtoList.size());

        bookingDtoList = bookingService.getOwnerBookings(userDto1.getId(), "PAST", 0, null);
        assertEquals(2, bookingDtoList.size());

        bookingDtoList = bookingService.getOwnerBookings(userDto1.getId(), "FUTURE", 0, null);
        assertEquals(0, bookingDtoList.size());
    }

    @Test
    void shouldThrowExceptionsWithUnknownState() {
        UserDto userDto1 = userService.createUser(user1);

        ValidationError exception = assertThrows(
                ValidationError.class,
                () -> bookingService.getOwnerBookings(userDto1.getId(), "TEST", 0, null)
        );

        assertEquals("Unknown state: TEST", exception.getMessage());

        exception = assertThrows(
                ValidationError.class,
                () -> bookingService.getBookings(userDto1.getId(), "TEST1", 0, null)
        );

        assertEquals("Unknown state: TEST1", exception.getMessage());
    }

    @Test
    void shouldDeclineBookingRequestByOwner() {
        UserDto userDto1 = userService.createUser(user1);
        UserDto userDto2 = userService.createUser(user2);

        ItemDto itemDto = itemService.createItem(userDto1.getId(), mapperService.toItem(item1));

        BookingInputDto bookingInputDto1 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2023, 2, 1, 0, 0),
                LocalDateTime.of(2023, 2, 2, 0, 0)
        );

        BookingDto bookingDto = bookingService.create(userDto2.getId(), bookingInputDto1);

        bookingService.update(userDto1.getId(), bookingDto.getId(), false);
    }

    @Test
    void shouldCancelBookingRequestByBooker() {
        UserDto userDto1 = userService.createUser(user1);
        UserDto userDto2 = userService.createUser(user2);

        ItemDto itemDto = itemService.createItem(userDto1.getId(), mapperService.toItem(item1));

        BookingInputDto bookingInputDto1 = new BookingInputDto(
                itemDto.getId(),
                LocalDateTime.of(2023, 2, 1, 0, 0),
                LocalDateTime.of(2023, 2, 2, 0, 0)
        );

        BookingDto bookingDto = bookingService.create(userDto2.getId(), bookingInputDto1);

        bookingService.update(userDto2.getId(), bookingDto.getId(), false);
    }
}
