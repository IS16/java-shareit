package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class BookingRepositoryJpaTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private Item item;

    private PageRequest page = PageRequest.of(0, 10);

    @BeforeEach
    void saveData() {
        owner = new User();
        owner.setName("Admin admin");
        owner.setEmail("admin@shareit.ru");
        em.persist(owner);

        booker = new User();
        booker.setName("Test test");
        booker.setEmail("test@shareit.ru");
        em.persist(booker);

        item = new Item();
        item.setName("Телескоп");
        item.setDescription("Очень мощный телескоп");
        item.setOwner(owner);
        item.setAvailable(true);
        em.persist(item);

        em.flush();
    }

    @Test
    void test_findByBookerId() {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(10));
        booking.setEnd(LocalDateTime.now().plusDays(20));
        em.persist(booking);
        em.flush();

        Page<Booking> resultBookings = bookingRepository.findByBookerId(booker.getId(), page);
        assertEquals(1, resultBookings.toList().size());
    }

    @Test
    void test_findByBookerIdAndStartIsBeforeAndEndIsAfter() {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.now().minusDays(3));
        booking.setEnd(LocalDateTime.now().plusDays(5));
        em.persist(booking);
        em.flush();

        Page<Booking> resultBookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(
                booker.getId(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                page
        );
        assertEquals(1, resultBookings.toList().size());
    }

    @Test
    void test_findByBookerIdAndEndIsBefore() {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.now().minusDays(7));
        booking.setEnd(LocalDateTime.now().minusDays(4));
        em.persist(booking);
        em.flush();

        Page<Booking> resultBookings = bookingRepository.findByBookerIdAndEndIsBefore(
                booker.getId(),
                LocalDateTime.now(),
                page
        );
        assertEquals(1, resultBookings.toList().size());
    }

    @Test
    void test_findByBookerIdAndStartIsAfter() {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.now().plusDays(3));
        booking.setEnd(LocalDateTime.now().plusDays(5));
        em.persist(booking);
        em.flush();

        Page<Booking> resultBookings = bookingRepository.findByBookerIdAndStartIsAfter(
                booker.getId(),
                LocalDateTime.now(),
                page
        );
        assertEquals(1, resultBookings.toList().size());
    }

    @Test
    void test_findByBookerIdAndStatus() {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.REJECTED);
        booking.setStart(LocalDateTime.now().plusDays(3));
        booking.setEnd(LocalDateTime.now().plusDays(5));
        em.persist(booking);
        em.flush();

        Page<Booking> resultBookings = bookingRepository.findByBookerIdAndStatus(
                booker.getId(),
                BookingStatus.REJECTED,
                page
        );
        assertEquals(1, resultBookings.toList().size());
    }

    @Test
    void test_findByItem_Owner_Id() {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.now().minusDays(3));
        booking.setEnd(LocalDateTime.now().plusDays(5));
        em.persist(booking);
        em.flush();

        Page<Booking> resultBookings = bookingRepository.findByItem_Owner_Id(
                owner.getId(),
                page
        );
        assertEquals(1, resultBookings.toList().size());
    }

    @AfterEach
    void deleteData() {
        bookingRepository.deleteAll();
    }
}
