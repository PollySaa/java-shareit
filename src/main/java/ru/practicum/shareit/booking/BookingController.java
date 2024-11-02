package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.constant.UserConstant;

import java.util.List;

@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    BookingService bookingService;

    @PostMapping
    public BookingInputDto createBooking(@RequestHeader(UserConstant.X_SHARER_USER_ID) Long bookerId,
                                    @RequestBody BookingDto bookingDto) {
        log.info("Выполнение createBooking");
        return bookingService.createBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{booking-id}")
    public BookingInputDto updateBooking(@RequestHeader(UserConstant.X_SHARER_USER_ID) Long userId,
                                    @PathVariable("booking-id") Long bookingId,
                                    @RequestParam Boolean approved) {
        log.info("Выполнение updateBooking");
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{booking-id}")
    public BookingInputDto getBookingById(@RequestHeader(UserConstant.X_SHARER_USER_ID) Long userId,
                                     @PathVariable("booking-id") Long bookingId) {
        log.info("Выполнение getBookingById");
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingInputDto> getBookings(@RequestHeader(UserConstant.X_SHARER_USER_ID) Long userId,
                                        @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("Выполнение getBookings");
        return bookingService.getBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingInputDto> getBookingsOwner(@RequestHeader(UserConstant.X_SHARER_USER_ID) Long userId,
                                             @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("Выполнение getBookingsOwner");
        return bookingService.getBookingsOwner(userId, state);
    }
}