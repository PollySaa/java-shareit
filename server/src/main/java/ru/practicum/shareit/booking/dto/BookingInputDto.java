package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingInputDto {
    Long id;
    String start;
    String end;
    String status;
    UserDto booker;
    ItemDto item;
}
