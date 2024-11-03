package ru.practicum.shareit.request;

import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestThingsDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requester(itemRequest.getRequester())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requester(itemRequestDto.getRequester())
                .created(itemRequestDto.getCreated())
                .build();
    }

    public static ItemRequestThingsDto toItemRequestThingsDto(ItemRequest itemRequest,
                                                              List<ItemResponseDto> itemResponseDto) {
        return ItemRequestThingsDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(toStringTime(itemRequest.getCreated()))
                .items(itemResponseDto != null ? List.copyOf(itemResponseDto) : null)
                .build();
    }

    private static String toStringTime(LocalDateTime created) {
        return DateTimeFormatter
                .ofPattern("yyyy.MM.dd HH:mm:ss")
                .withZone(ZoneOffset.UTC)
                .format(created);
    }
}
