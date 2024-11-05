package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constant.UserConstant;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(UserConstant.X_SHARER_USER_ID) long userId,
                                             @Valid @RequestBody ItemDto itemDto) {
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{item-id}")
    public ResponseEntity<Object> updateItem(@RequestHeader(UserConstant.X_SHARER_USER_ID) Long userId,
                                             @PathVariable("item-id") Long itemId,
                                             @RequestBody ItemDto itemUpdateDtoDto) {
        return itemClient.updateItem(userId, itemId, itemUpdateDtoDto);
    }

    @GetMapping("/{item-id}")
    public ResponseEntity<Object> getItem(@RequestHeader(UserConstant.X_SHARER_USER_ID) long userId,
                                          @PathVariable("item-id") long itemId) {
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader(UserConstant.X_SHARER_USER_ID) long userId) {
        return itemClient.getAllItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader(UserConstant.X_SHARER_USER_ID) long userId,
                                              @NotNull @RequestParam String text) {
        return itemClient.searchItems(userId, text);
    }

    @PostMapping("/{item-id}/comment")
    public ResponseEntity<Object> createCommit(@RequestHeader(UserConstant.X_SHARER_USER_ID) Long userId,
                                               @PathVariable("item-id") Long itemId,
                                               @Valid @RequestBody CommentDto commentDto) {
        return itemClient.createComment(userId, itemId, commentDto);
    }
}
