package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemCommentsDateDto {
    Long id;
    String name;
    String description;
    Boolean available;
    LocalDateTime start;
    LocalDateTime end;
    List<CommentDto> comments;
}
