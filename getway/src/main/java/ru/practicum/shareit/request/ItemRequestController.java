package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constant.UserConstant;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                    @RequestHeader(UserConstant.X_SHARER_USER_ID) long userId) {
        return itemRequestClient.addRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader(UserConstant.X_SHARER_USER_ID) long userId) {
        return itemRequestClient.getAllRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllFromUser(@RequestHeader(UserConstant.X_SHARER_USER_ID) long userId) {
        return itemRequestClient.getAllByUser(userId);
    }

    @GetMapping("/{request-id}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader(UserConstant.X_SHARER_USER_ID) long userId,
                                                 @PathVariable("request-id") long requestId) {
        return itemRequestClient.getItemRequest(userId, requestId);
    }
}
