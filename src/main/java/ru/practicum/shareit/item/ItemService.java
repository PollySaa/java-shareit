package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ItemService {
    ItemStorage itemStorage;
    ItemMapper itemMapper;
    UserService userService;

    public ItemDto addItem(Integer ownerId, ItemDto itemDto) {
        if (userService.getUserById(ownerId) != null) {
            itemDto = itemMapper.toItemDto(itemStorage.addItem(itemMapper.toItem(itemDto, ownerId)));
        }
        return itemDto;
    }

    public ItemDto updateItem(Integer ownerId, Integer itemId, ItemDto itemDto) {
        if (itemDto.getId() == null) {
            itemDto.setId(itemId);
        }

        Item oldItem = itemStorage.getItemById(itemId);
        if (!oldItem.getOwnerId().equals(ownerId)) {
            throw new NotFoundException("У пользователя нет такой вещи!");
        }

        if (userService.getUserById(ownerId) != null) {
            itemDto = itemMapper.toItemDto(itemStorage.updateItem(itemMapper.toItem(itemDto, ownerId)));
        }

        return itemDto;
    }

    public ItemDto getItemById(Integer itemId) {
        return itemMapper.toItemDto(itemStorage.getItemById(itemId));
    }

    public List<ItemDto> getItemsByOwnerId(Integer ownerId) {
        return itemStorage.getItemsByOwnerId(ownerId).stream()
                .map(itemMapper::toItemDto)
                .collect(toList());
    }

    public List<ItemDto> searchItem(String text) {
        text = text.toLowerCase();
        return itemStorage.searchItem(text).stream()
                .map(itemMapper::toItemDto)
                .collect(toList());
    }
}
