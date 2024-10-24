package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@UtilityClass
public class BookingMapper {

    public static Booking toBooking(BookingDto bookingDto, Item item, User user) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(user)
                .status(Status.valueOf(bookingDto.getStatus()))
                .build();
    }

    public static List<BookingInputDto> mapToBookingInputDtoList(List<Booking> bookings) {
        return bookings.stream()
                .map(booking -> {
                    ItemDto itemDto = ItemMapper.toItemDto(booking.getItem());
                    UserDto userDto = UserMapper.toUserDto(booking.getBooker());
                    return toBookingInputDto(booking, userDto, itemDto);
                })
                .toList();
    }

    public static BookingInputDto toBookingInputDto(Booking booking, UserDto userDto, ItemDto itemDto) {
        return BookingInputDto.builder()
                .id(booking.getId())
                .start(booking.getStart().toString())
                .end(booking.getEnd().toString())
                .status(booking.getStatus().name())
                .booker(userDto)
                .item(itemDto)
                .build();
    }
}
