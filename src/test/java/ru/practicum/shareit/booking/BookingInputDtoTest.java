package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingInputDtoTest {
    @Autowired
    private JacksonTester<BookingInputDto> json;

    private BookingInputDto bookingInputDto = new BookingInputDto(
            1L,
            LocalDateTime.of(2023, 2, 1, 0, 0),
            LocalDateTime.of(2023, 2, 3, 0, 0)
    );

    @Test
    public void datesSerialize() throws Exception {
        assertThat(json.write(bookingInputDto))
                .extractingJsonPathStringValue("$.start")
                .isEqualTo("2023-02-01T00:00:00");

        assertThat(json.write(bookingInputDto))
                .extractingJsonPathStringValue("$.end")
                .isEqualTo("2023-02-03T00:00:00");
    }
}
