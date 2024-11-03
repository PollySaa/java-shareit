package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestThingsDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addRequest(Long userId, ItemRequestDto itemRequestDto);

    ItemRequestThingsDto get(Long requestId);

    List<ItemRequestThingsDto> getAllRequests(Long userId);

    List<ItemRequestDto> getAllByUser(Long userId);
}
