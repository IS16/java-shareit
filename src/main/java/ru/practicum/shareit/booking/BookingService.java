package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, BookingInputDto bookingDto);

    BookingDto update(Long userId, Long bookingId, Boolean approved);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getBookings(Long userId, String state);
    
    List<BookingDto> getOwnerBookings(Long userId, String state);
}
