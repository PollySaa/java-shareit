package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestThingsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    ItemRequestRepository itemRequestRepository;
    ItemRepository itemRepository;
    UserRepository userRepository;

    @Override
    public ItemRequestDto addRequest(Long userId, ItemRequestDto itemRequestDto) {
        User requesting = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден c id = " + userId));
        itemRequestDto.setRequester(requesting);
        itemRequestDto.setCreated(LocalDateTime.now());

        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(ItemRequestMapper
                .toItemRequest(itemRequestDto)));
    }

    @Override
    public ItemRequestThingsDto get(Long requestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
            new ValidationException("Запроса с таким id = " + requestId + " нет!"));

        List<Item> items = itemRepository.findByRequestId(requestId);
        List<ItemResponseDto> itemResponses = ItemMapper.toItemResponseDtoList(items);

        return ItemRequestMapper.toItemRequestThingsDto(itemRequest, itemResponses);
    }

    @Override
    public List<ItemRequestThingsDto> getAllRequests(Long userId) {
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequester_Id(userId);

        List<Item> itemList = itemRepository.findItemsByRequestIds(itemRequests.stream()
                .map(ItemRequest::getId).collect(Collectors.toList()));

        return itemRequests.stream()
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .map(itemRequest -> ItemRequestMapper.toItemRequestThingsDto(
                        itemRequest, getItemResponseMap(itemList).get(itemRequest.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllByUser(Long userId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdNot(userId, sort);

        return requests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .toList();
    }

    private Map<Long, List<ItemResponseDto>> getItemResponseMap(List<Item> itemList) {
        return itemList.stream()
                .collect(Collectors.groupingBy(Item::getRequestId,
                        Collectors.mapping(ItemMapper::toItemResponseDto, Collectors.toList())));
    }
}
