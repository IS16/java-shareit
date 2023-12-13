package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.exceptions.BookingNotFound;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.error.baseExceptions.ValidationError;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.service.MapperService;
import ru.practicum.shareit.service.ValidationService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final ValidationService validationService;
    private final MapperService mapperService;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDto create(Long userId, BookingInputDto bookingDto) {
        UserDto user = validateUser(userId);
        ItemDto item = validationService.getItemById(bookingDto.getItemId(), userId);

        if (!item.getAvailable()) {
            throw new ValidationError("Данная вещь недоступна для бронирования");
        }

        if (!bookingDto.getStart().isBefore(bookingDto.getEnd())) {
            throw new ValidationError("Дата окончания бранирования не может быть раньше, чем дата начала");
        }

        Booking booking = mapperService.toBooking(bookingDto, item, user);
        if (userId.equals(booking.getItem().getOwner().getId())) {
            throw new BookingNotFound("Владелец вещи не может забронировать собственную вещь");
        }

        return mapperService.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto update(Long userId, Long bookingId, Boolean approved) {
        validateUser(userId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFound(String.format("Бронирование с id = %d не найдено", bookingId)));

        if (booking.getBooker().getId().equals(userId)) {
            if (!approved) {
                booking.setStatus(BookingStatus.CANCELED);
            } else {
                throw new BookingNotFound("Подтвердить бронирование может только владелец вещи");
            }
        } else if (booking.getItem().getOwner().getId().equals(userId)) {
            if (booking.getStatus().equals(BookingStatus.CANCELED)) {
                throw new ValidationError("Бронирование было отменено");
            } else if (!booking.getStatus().equals(BookingStatus.WAITING)) {
                throw new ValidationError("Решение по бронированию уже принято");
            }

            if (approved) {
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }
        } else {
            throw new BookingNotFound("Подтвердить бронирование может только владелец вещи");
        }

        return mapperService.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        validateUser(userId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFound(String.format("Бронирование с id = %d не найдено", bookingId)));

        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return mapperService.toBookingDto(booking);
        } else {
            throw new BookingNotFound("Посмотреть данные бронирования могут только владелец вещи и создатель бронирования");
        }
    }

    @Override
    public List<BookingDto> getBookings(Long userId, String state) {
        validateUser(userId);

        List<Booking> bookings;
        Sort sortByStart = Sort.by(Sort.Direction.DESC, "start");

        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByBookerId(userId, sortByStart);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now(), sortByStart);
                break;
            case "PAST":
                bookings = bookingRepository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), sortByStart);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), sortByStart);
                break;
            case "WAITING":
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.valueOf(state), sortByStart);
                break;
            default:
                throw new ValidationError("Unknown state: " + state);
        }

        return bookings.stream()
                .map(mapperService::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long userId, String state) {
        validateUser(userId);

        List<Booking> bookings;
        Sort sortByStart = Sort.by(Sort.Direction.DESC, "start");

        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByItem_Owner_Id(userId, sortByStart);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now(), sortByStart);
                break;
            case "PAST":
                bookings = bookingRepository.findByItem_Owner_IdAndEndIsBefore(userId, LocalDateTime.now(), sortByStart);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByItem_Owner_IdAndStartIsAfter(userId, LocalDateTime.now(), sortByStart);
                break;
            case "WAITING":
            case "REJECTED":
                bookings = bookingRepository.findByItem_Owner_IdAndStatus(userId, BookingStatus.valueOf(state), sortByStart);
                break;
            default:
                throw new ValidationError("Unknown state: " + state);
        }

        return bookings.stream()
                .map(mapperService::toBookingDto)
                .collect(Collectors.toList());
    }

    private UserDto validateUser(Long userId) {
        return validationService.getUserById(userId);
    }
}
