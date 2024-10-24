package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import java.util.List;

@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    BookingService bookingService;

    @PostMapping
    public BookingInputDto createBooking(@RequestHeader(X_SHARER_USER_ID) Long bookerId,
                                    @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{booking-id}")
    public BookingInputDto updateBooking(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                    @PathVariable("booking-id") Long bookingId,
                                    @RequestParam Boolean approved) {
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{booking-id}")
    public BookingInputDto getBookingById(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                     @PathVariable("booking-id") Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingInputDto> getBookings(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                        @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.getBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingInputDto> getBookingsOwner(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                             @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.getBookingsOwner(userId, state);
    }
}