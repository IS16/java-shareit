package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.error.baseExceptions.ValidationError;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody BookingInputDto bookingDto) {
		return bookingClient.createBooking(userId, bookingDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId, @RequestParam Boolean approved) {
		log.info("Update approved {}, userId={}", approved, userId);
		return bookingClient.updateBooking(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBookingById(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getBookings(
			@RequestHeader("X-Sharer-User-Id") Long userId,
			@RequestParam(defaultValue = "ALL") String state,
			@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
			@Positive @RequestParam(defaultValue = "10") Integer size
	) {
		BookingState status = BookingState.from(state)
				.orElseThrow(() -> new ValidationError("Unknown state: " + state));
		log.info("Get booking with state {}, userId={}, from={}, size={}", state, userId, from, size);
		return bookingClient.getBookings(userId, status, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getOwnerBookings(
			@RequestHeader("X-Sharer-User-Id") Long userId,
			@RequestParam(defaultValue = "ALL") String state,
			@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
			@Positive @RequestParam(defaultValue = "10") Integer size
	) {
		BookingState status = BookingState.from(state)
				.orElseThrow(() -> new ValidationError("Unknown state: " + state));
		log.info("Get owner booking with state {}, userId={}, from={}, size={}", state, userId, from, size);
		return bookingClient.getOwnerBookings(userId, status, from, size);
	}
}
