package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

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
    public ItemDto addItem(@RequestHeader(X_SHARER_USER_ID) Long userId, @RequestBody ItemDto itemDto) {
        log.info("Выполнение addItem");
        return itemService.addItem(userId, itemDto);
    }

    @ResponseBody
    @PatchMapping("/{item-id}")
    public ItemDto updateItem(@RequestHeader(X_SHARER_USER_ID) Long userId, @PathVariable("item-id") Long itemId,
                              @RequestBody ItemUpdateDto itemUpdateDto) {
        log.info("Выполнение updateItem");
        return itemService.updateItem(userId, itemId, itemUpdateDto);
    }

    @GetMapping("/{item-id}")
    public ItemCommentDto getItemById(@PathVariable("item-id") Long itemId) {
        log.info("Выполнение getItemById");
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwnerId(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        log.info("Выполнение getItemsByOwnerId");
        return itemService.getItemsByOwnerId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySearchQuery(@RequestParam String text) {
        log.info("Выполнение getItemsBySearchQuery");
        return itemService.searchItems(text);
    }

    @ResponseBody
    @PostMapping("/{item-id}/comment")
    public CommentDto createComment(@RequestHeader(X_SHARER_USER_ID) Long userId, @PathVariable("item-id") Long itemId,
                                    @RequestBody CommentDto commentDto) {
        log.info("Выполнение createComment");
        return itemService.createComment(userId, itemId, commentDto);
    }
}
