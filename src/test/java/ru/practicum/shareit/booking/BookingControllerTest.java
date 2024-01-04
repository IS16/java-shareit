package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private BookingInputDto bookingInputDto = new BookingInputDto(
            1L,
            LocalDateTime.of(2024, 2, 1, 0, 0),
            LocalDateTime.of(2024, 12, 31, 23, 59)
    );

    private BookingDto bookingDto = new BookingDto(
            1L,
            LocalDateTime.of(2024, 2, 1, 0, 0),
            LocalDateTime.of(2024, 12, 31, 23, 59),
            new ItemDto(
                    1L,
                    "Тестовый предмет",
                    "Описание",
                    true,
                    new UserDto(
                            1L,
                            "Admin",
                            "admin@shareit.ru"
                    ),
                    null, null, null, null
            ),
            new UserDto(2L, "Test", "testuser@shareit.ru"),
            BookingStatus.WAITING
    );

    @Test
    void createBooking() throws Exception {
        when(bookingService.create(any(), any(BookingInputDto.class))).thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                .content(mapper.writeValueAsString(bookingInputDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-id", 1)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath(
                        "$.start",
                        is(bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath(
                        "$.end",
                        is(bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), BookingStatus.class));
    }

    @Test
    void getBooking() throws Exception {
        when(bookingService.getBookingById(any(Long.class), any(Long.class))).thenReturn(bookingDto);

        mvc.perform(get("/bookings/1")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-id", 1)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath(
                        "$.start",
                        is(bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath(
                        "$.end",
                        is(bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), BookingStatus.class));
    }

    @Test
    void getBookings() throws Exception {
        when(bookingService.getBookings(
                any(Long.class),
                any(String.class),
                any(Integer.class),
                nullable(Integer.class))).thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath(
                        "$.[0].start",
                        is(bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath(
                        "$.[0].end",
                        is(bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath(
                        "$.[0].item.id",
                        is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath(
                        "$.[0].booker.id",
                        is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].status", is(bookingDto.getStatus().toString()), BookingStatus.class));
    }

    @Test
    void updateBooking() throws Exception {
        when(bookingService.update(any(Long.class), any(Long.class), any(Boolean.class)))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/1")
                .content(mapper.writeValueAsString(bookingDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1)
                .queryParam("approved", "true")
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath(
                        "$.start",
                        is(bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath(
                        "$.end",
                        is(bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), BookingStatus.class));
    }

    @Test
    void getBookingsByOwner() throws Exception {
        when(bookingService.getOwnerBookings(
                any(Long.class),
                any(String.class),
                any(Integer.class),
                nullable(Integer.class))).thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner?from=0&size=10")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath(
                        "$.[0].start",
                        is(bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath(
                        "$.[0].end",
                        is(bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath(
                        "$.[0].item.id",
                        is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath(
                        "$.[0].booker.id",
                        is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].status", is(bookingDto.getStatus().toString()), BookingStatus.class));
    }
}
