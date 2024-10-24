package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import java.util.List;

public interface BookingService {
    BookingInputDto createBooking(Long bookerId, BookingDto bookingDto);

    BookingInputDto updateBooking(Long userId, Long bookingId, Boolean approved);

    BookingInputDto getBookingById(Long userId, Long bookingId);

    List<BookingInputDto> getBookings(Long userId, String state);

    List<BookingInputDto> getBookingsOwner(Long userId, String state);
}
