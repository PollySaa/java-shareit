package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constant.UserConstant;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestThingsDto;

import java.util.List;

@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/requests")
public class ItemRequestController {
    ItemRequestService itemRequestService;

    @ResponseBody
    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader(UserConstant.X_SHARER_USER_ID) Long userId,
                                     @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Выполнение addRequest");
        return itemRequestService.addRequest(userId, itemRequestDto);
    }

    @GetMapping("/{request-id}")
    public ItemRequestThingsDto get(@PathVariable("request-id") Long requestId) {
        log.info("Выполнение get");
        return itemRequestService.get(requestId);
    }

    @GetMapping
    public List<ItemRequestThingsDto> getAllRequests(@RequestHeader(UserConstant.X_SHARER_USER_ID) Long userId) {
        log.info("Выполнение getAllRequests");
        return itemRequestService.getAllRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllFromUser(@RequestHeader(UserConstant.X_SHARER_USER_ID) Long userId) {
        log.info("Выполнение getAllFromUser");
        return itemRequestService.getAllByUser(userId);
    }
}
