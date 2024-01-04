package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.utils.Pagination;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public List<BookingDto> getBookings(Long userId, String state, Integer from, Integer size) {
        validateUser(userId);

        List<BookingDto> listBookingDto = new ArrayList<>();

        Pageable pageable;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Page<Booking> page;
        Pagination pager = new Pagination(from, size);

        if (size == null) {
            pageable = PageRequest.of(pager.getPageStart(), pager.getPageSize(), sort);
            page = getBookingsPage(state, userId, pageable);

            while (page.hasContent()) {
                listBookingDto.addAll(page.stream().map(mapperService::toBookingDto).collect(Collectors.toList()));
                pageable = pageable.next();
                page = getBookingsPage(state, userId, pageable);
            }
        } else {
            for (int i = pager.getPageStart(); i < pager.getPagesAmount(); i++) {
                pageable = PageRequest.of(i, pager.getPageSize(), sort);
                page = getBookingsPage(state, userId, pageable);
                listBookingDto.addAll(page.stream().map(mapperService::toBookingDto).collect(Collectors.toList()));
            }

            listBookingDto = listBookingDto.stream().limit(size).collect(Collectors.toList());
        }

        return listBookingDto;
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long userId, String state, Integer from, Integer size) {
        validateUser(userId);

        List<BookingDto> listBookingDto = new ArrayList<>();

        Pageable pageable;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Page<Booking> page;
        Pagination pager = new Pagination(from, size);

        if (size == null) {
            pageable = PageRequest.of(pager.getPageStart(), pager.getPageSize(), sort);
            page = getOwnerBookingsPage(state, userId, pageable);

            while (page.hasContent()) {
                listBookingDto.addAll(page.stream().map(mapperService::toBookingDto).collect(Collectors.toList()));
                pageable = pageable.next();
                page = getOwnerBookingsPage(state, userId, pageable);
            }
        } else {
            for (int i = pager.getPageStart(); i < pager.getPagesAmount(); i++) {
                pageable = PageRequest.of(i, pager.getPageSize(), sort);
                page = getOwnerBookingsPage(state, userId, pageable);
                listBookingDto.addAll(page.stream().map(mapperService::toBookingDto).collect(Collectors.toList()));
            }

            listBookingDto = listBookingDto.stream().limit(size).collect(Collectors.toList());
        }

        return listBookingDto;
    }

    private Page<Booking> getBookingsPage(String state, Long userId, Pageable pageable) {
        Page<Booking> page;

        switch (state) {
            case "ALL":
                page = bookingRepository.findByBookerId(userId, pageable);
                break;
            case "CURRENT":
                page = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            case "PAST":
                page = bookingRepository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), pageable);
                break;
            case "FUTURE":
                page = bookingRepository.findByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), pageable);
                break;
            case "WAITING":
            case "REJECTED":
                page = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.valueOf(state), pageable);
                break;
            default:
                throw new ValidationError("Unknown state: " + state);
        }

        return page;
    }

    private Page<Booking> getOwnerBookingsPage(String state, Long userId, Pageable pageable) {
        Page<Booking> page;

        switch (state) {
            case "ALL":
                page = bookingRepository.findByItem_Owner_Id(userId, pageable);
                break;
            case "CURRENT":
                page = bookingRepository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            case "PAST":
                page = bookingRepository.findByItem_Owner_IdAndEndIsBefore(userId, LocalDateTime.now(), pageable);
                break;
            case "FUTURE":
                page = bookingRepository.findByItem_Owner_IdAndStartIsAfter(userId, LocalDateTime.now(), pageable);
                break;
            case "WAITING":
            case "REJECTED":
                page = bookingRepository.findByItem_Owner_IdAndStatus(userId, BookingStatus.valueOf(state), pageable);
                break;
            default:
                throw new ValidationError("Unknown state: " + state);
        }

        return page;
    }

    private UserDto validateUser(Long userId) {
        return validationService.getUserById(userId);
    }
}
