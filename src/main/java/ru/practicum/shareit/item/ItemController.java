package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/items")
public class ItemController {
    ItemService itemService;
    static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @ResponseBody
    @PostMapping
    public ItemDto addItem(@RequestHeader(X_SHARER_USER_ID) Integer userId, @RequestBody ItemDto itemDto) {
        return itemService.addItem(userId, itemDto);
    }

    @ResponseBody
    @PatchMapping("/{item-id}")
    public ItemDto updateItem(@RequestHeader(X_SHARER_USER_ID) Integer userId, @PathVariable("item-id") Integer itemId,
                              @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{item-id}")
    public ItemDto getItemById(@PathVariable("item-id") Integer itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwnerId(@RequestHeader(X_SHARER_USER_ID) Integer userId) {
        return itemService.getItemsByOwnerId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        return itemService.searchItem(text);
    }
}
