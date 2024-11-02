package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestThingsDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestThingsDto> getAll(Long userId);

    List<ItemRequestDto> getAllFromUser(Long userId);

    ItemRequestThingsDto get(Long requestId);
}
