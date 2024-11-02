package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
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
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemRequestServiceImpl implements ItemRequestService {
    ItemRequestRepository itemRequestRepository;
    ItemRepository itemRepository;
    UserRepository userRepository;

    @Override
    public ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ValidationException("Отсутсвует автор с id = " + userId));

        itemRequestDto.setCreated(LocalDateTime.now());
        itemRequestDto.setRequester(user);

        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(ItemRequestMapper
                .toItemRequest(itemRequestDto)));
    }

    @Override
    public ItemRequestThingsDto get(Long requestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                     new ValidationException("Нет запроса с id = " + requestId));

        List<Item> items = itemRepository.findByRequest_Id(requestId);
        List<ItemResponseDto> itemResponses = ItemMapper.toItemResponseDtoList(items);

        return ItemRequestMapper.toItemRequestThingsDto(itemRequest, itemResponses);
    }

    @Override
    public List<ItemRequestThingsDto> getAll(Long userId) {
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
    public List<ItemRequestDto> getAllFromUser(Long userId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdNot(userId, sort);

        return requests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .toList();
    }

    private Map<Long, List<ItemResponseDto>> getItemResponseMap(List<Item> itemList) {
        return itemList.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId(),
                        Collectors.mapping(ItemMapper::toItemResponseDto, Collectors.toList())));
    }
}
