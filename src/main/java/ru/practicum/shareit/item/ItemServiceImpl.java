package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    ItemRepository itemRepository;
    UserRepository userRepository;
    BookingRepository bookingRepository;
    CommentRepository commentRepository;
    ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto addItem(Long ownerId, ItemDto itemDto) {
        if (itemDto.getName().isEmpty() || itemDto.getDescription().isEmpty() || itemDto.getAvailable() == null) {
            throw new ValidationException("Некорректные данные!");
        }
        ItemRequest itemRequest = null;
        Long requestId = itemDto.getRequestId();
        if (requestId != null) {
            itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                 new NotFoundException("Запроса с id = " + requestId + " нет"));
        }
        User user = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.save(ItemMapper.toItem(itemDto, user, itemRequest));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long ownerId, Long itemId, ItemUpdateDto itemUpdateDto) {
        userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item newItem = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с id = " + itemId + " не найден"));
        if (!newItem.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Указан другой пользователь");
        }
        if (itemUpdateDto.getName() != null && !itemUpdateDto.getName().isEmpty()) {
            newItem.setName(itemUpdateDto.getName());
        }
        if (itemUpdateDto.getDescription() != null && !itemUpdateDto.getDescription().isEmpty()) {
            newItem.setDescription(itemUpdateDto.getDescription());
        }
        if (itemUpdateDto.getAvailable() != null) {
            newItem.setAvailable(itemUpdateDto.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.save(newItem));
    }

    @Override
    public ItemCommentDto getItemById(Long itemId) {
        List<CommentDto> commentDto = CommentMapper.toCommentDtoList(commentRepository.findByItemId(itemId));
        return ItemMapper.toItemCommentDto(itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найден")), commentDto);
    }

    @Override
    public List<ItemDto> getItemsByOwnerId(Long ownerId) {
        return itemRepository.findItemsByOwnerId(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text)
                .stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        User author = userRepository.findById(userId).orElseThrow(() ->
                new ValidationException("Отсутсвует автор с id = " + userId));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Отсутсвует вещь с id = " + itemId));
        Booking booking = bookingRepository.findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(itemId,
                userId, LocalDateTime.now(), Status.APPROVED);

        if (booking == null) {
            throw new ValidationException("Пользователь не бронировал вещь!");
        }
        commentDto.setCreated(LocalDateTime.now());
        Comment comment = commentRepository.save(CommentMapper.toComment(commentDto, author, item));
        return CommentMapper.toCommentDto(comment);
    }
}