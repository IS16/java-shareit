package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

public interface BookingRepository extends PagingAndSortingRepository<Booking, Long> {
    Page<Booking> findByBookerId(Long bookerId, Pageable page);

    Page<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start, LocalDateTime end, Pageable page);

    Page<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Pageable page);

    Page<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime start, Pageable page);

    Page<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Pageable page);

    Page<Booking> findByItem_Owner_Id(Long ownerId, Pageable page);

    Page<Booking> findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(Long ownerId, LocalDateTime start, LocalDateTime end, Pageable page);

    Page<Booking> findByItem_Owner_IdAndEndIsBefore(Long ownerId, LocalDateTime end, Pageable page);

    Page<Booking> findByItem_Owner_IdAndStartIsAfter(Long ownerId, LocalDateTime start, Pageable page);

    Page<Booking> findByItem_Owner_IdAndStatus(Long ownerId, BookingStatus status, Pageable page);

    Booking findFirstByItem_IdAndStartBeforeAndStatusOrderByStartDesc(Long itemId, LocalDateTime end, BookingStatus status);

    Booking findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime end, BookingStatus status);

    Booking findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(Long itemId, Long userId, LocalDateTime end, BookingStatus status);
}
