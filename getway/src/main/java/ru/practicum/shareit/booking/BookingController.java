package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.validator.ValidBookingState;
import ru.practicum.shareit.constant.UserConstant;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> createBooking(@RequestHeader(UserConstant.X_SHARER_USER_ID) long userId,
												@RequestBody @Valid BookItemRequestDto requestDto) {
		return bookingClient.createBooking(userId, requestDto);
	}

	@PatchMapping("/{booking-id}")
	public ResponseEntity<Object> updateBooking(@RequestHeader(UserConstant.X_SHARER_USER_ID) long userId,
												@PathVariable("booking-id") Long bookingId,
												@RequestParam boolean approved) {
		return bookingClient.updateBooking(userId, bookingId, approved);
	}

	@GetMapping
	public ResponseEntity<Object> getAllBookings(@RequestHeader(UserConstant.X_SHARER_USER_ID) long userId,
											  @RequestParam(name = "state", defaultValue = "all") String stateParam,
											  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
											  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		return bookingClient.getAllBookings(userId, state, from, size);
	}

	@GetMapping("/{booking-id}")
	public ResponseEntity<Object> getBooking(@RequestHeader(UserConstant.X_SHARER_USER_ID) long userId,
											 @PathVariable("booking-id") Long bookingId) {
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingsOwner(
			@RequestHeader(UserConstant.X_SHARER_USER_ID) Long userId,
			@RequestParam(required = false, defaultValue = "ALL") @ValidBookingState String state) {
		return bookingClient.getBookingOwner(userId, state);
	}
}