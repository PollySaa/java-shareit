package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.User;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemCommentDto {
    Long id;
    String name;
    String description;
    User lastBooking;
    User nextBooking;
    Boolean available;
    List<CommentDto> comments;
}
