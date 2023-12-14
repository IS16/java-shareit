package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;


@RequiredArgsConstructor
@Component
public class BookingMapper {
    public BookingDto toBookingDto(Booking booking, ItemDto itemDto, UserDto userDto) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                itemDto,
                userDto,
                booking.getStatus()
        );
    }

    public Booking toBooking(BookingDto bookingDto, Item item, User user) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                user,
                bookingDto.getStatus()
        );
    }

    public Booking toBooking(BookingInputDto bookingInputDto, Item item, User user) {
        return new Booking(
                null,
                bookingInputDto.getStart(),
                bookingInputDto.getEnd(),
                item,
                user,
                BookingStatus.WAITING
        );
    }

    public BookingOutputDto toBookingOutputDto(Booking booking) {
        return new BookingOutputDto(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getStart(),
                booking.getEnd()
        );
    }
}
