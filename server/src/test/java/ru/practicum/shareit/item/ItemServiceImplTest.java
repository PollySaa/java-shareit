package ru.practicum.shareit.item;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class ItemServiceImplTest {
    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User testUser;
    private ItemDto itemDto1;
    private ItemDto itemDto2;
    private Item item;
    private Booking booking;
    private Comment comment;
    private ItemRequest itemRequest;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        testUser = User.builder()
                .name("Test User")
                .email("test@example.com")
                .build();
        testUser = userRepository.save(testUser);

        itemDto1 = ItemDto.builder()
                .name("Test Item 1")
                .description("Test Description 1")
                .available(true)
                .build();

        itemDto2 = ItemDto.builder()
                .name("Test Item 2")
                .description("Test Description 2")
                .available(false)
                .build();

        item = Item.builder()
                .owner(testUser)
                .name("Test Item")
                .description("Test Description")
                .available(false)
                .build();
        item = itemRepository.save(item);

        booking = Booking.builder()
                .status(Status.APPROVED)
                .item(item)
                .booker(testUser)
                .start(LocalDateTime.now().plusMinutes(5))
                .end(LocalDateTime.now().plusMinutes(10))
                .build();
        booking = bookingRepository.save(booking);

        comment = Comment.builder()
                .text("Test Comment")
                .item(item)
                .author(testUser)
                .createdDate(LocalDateTime.now())
                .build();
        comment = commentRepository.save(comment);

        itemRequest = ItemRequest.builder()
                .description("Test Request Description")
                .requester(testUser)
                .created(LocalDateTime.now())
                .build();
        itemRequest = itemRequestRepository.save(itemRequest);
    }

    @Test
    void testAddItem() {
        ItemDto itemDto = ItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        ItemDto savedItemDto = itemService.addItem(testUser.getId(), itemDto);

        assertNotNull(savedItemDto.getId());
        assertEquals(itemDto.getName(), savedItemDto.getName());
        assertEquals(itemDto.getDescription(), savedItemDto.getDescription());
        assertEquals(itemDto.getAvailable(), savedItemDto.getAvailable());
        assertEquals(testUser.getId(), savedItemDto.getOwner());
    }

    @Test
    void testUpdateItem() {
        ItemDto itemDto = ItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        ItemDto savedItemDto = itemService.addItem(testUser.getId(), itemDto);

        ItemUpdateDto updateDto = ItemUpdateDto.builder()
                .name("Updated Name")
                .description("Updated Description")
                .available(false)
                .build();

        ItemDto updatedItemDto = itemService.updateItem(testUser.getId(), savedItemDto.getId(), updateDto);

        assertNotNull(updatedItemDto.getId());
        assertEquals(updateDto.getName(), updatedItemDto.getName());
        assertEquals(updateDto.getDescription(), updatedItemDto.getDescription());
        assertEquals(updateDto.getAvailable(), updatedItemDto.getAvailable());
        assertEquals(testUser.getId(), updatedItemDto.getOwner());
    }

    @Test
    void testGetItemById() {
        ItemDto itemDto = ItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        ItemDto savedItemDto = itemService.addItem(testUser.getId(), itemDto);

        ItemCommentDto retrievedItemDto = itemService.getItemById(savedItemDto.getId());

        assertNotNull(retrievedItemDto);
        assertEquals(savedItemDto.getId(), retrievedItemDto.getId());
        assertEquals(savedItemDto.getName(), retrievedItemDto.getName());
        assertEquals(savedItemDto.getDescription(), retrievedItemDto.getDescription());
        assertEquals(savedItemDto.getAvailable(), retrievedItemDto.getAvailable());
        assertTrue(retrievedItemDto.getComments().isEmpty());
    }

    @Test
    void testSearchItems() {
        itemService.addItem(testUser.getId(), itemDto1);
        itemService.addItem(testUser.getId(), itemDto2);

        List<ItemDto> foundItems = itemService.searchItems("Test");

        assertNotNull(foundItems);
        assertEquals(1, foundItems.size());
        assertEquals("Test Item 1", foundItems.getFirst().getName());
    }

    @Test
    void testCreateComment() {
        assertEquals(comment.getText(), comment.getText());
        assertEquals(comment.getAuthor(), comment.getAuthor());
        assertEquals(comment.getCreatedDate(), comment.getCreatedDate());
        assertEquals(comment.getItem(), comment.getItem());
        assertEquals(comment.getAuthor(), comment.getAuthor());
        assertEquals(comment.getCreatedDate(), comment.getCreatedDate());
    }

    @Test
    void addItemShouldThrowValidationExceptionWhenItemDtoIsNull() {
        assertThrows(ValidationException.class, () -> {
            itemService.addItem(testUser.getId(), null);
        });
    }

    @Test
    void addItemShouldThrowValidationExceptionWhenItemDtoHasInvalidData() {
        ItemDto invalidItemDto = ItemDto.builder()
                .name("")
                .description("")
                .available(null)
                .build();

        assertThrows(ValidationException.class, () -> {
            itemService.addItem(testUser.getId(), invalidItemDto);
        });
    }

    @Test
    void addItemShouldThrowNotFoundExceptionWhenUserNotFound() {
        assertThrows(NotFoundException.class, () -> {
            itemService.addItem(999L, itemDto1);
        });
    }

    @Test
    void updateItemShouldThrowNotFoundExceptionWhenUserNotFound() {
        ItemDto itemDto = ItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        ItemDto savedItemDto = itemService.addItem(testUser.getId(), itemDto);

        ItemUpdateDto updateDto = ItemUpdateDto.builder()
                .name("Updated Name")
                .description("Updated Description")
                .available(false)
                .build();

        assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(999L, savedItemDto.getId(), updateDto);
        });
    }

    @Test
    void updateItemShouldThrowNotFoundExceptionWhenItemNotFound() {
        ItemUpdateDto updateDto = ItemUpdateDto.builder()
                .name("Updated Name")
                .description("Updated Description")
                .available(false)
                .build();

        assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(testUser.getId(), 999L, updateDto);
        });
    }

    @Test
    void updateItemShouldThrowNotFoundExceptionWhenUserIsNotOwner() {
        ItemDto itemDto = ItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        ItemDto savedItemDto = itemService.addItem(testUser.getId(), itemDto);

        User anotherUser = User.builder()
                .name("Another User")
                .email("another@test.com")
                .build();
        anotherUser = userRepository.save(anotherUser);

        ItemUpdateDto updateDto = ItemUpdateDto.builder()
                .name("Updated Name")
                .description("Updated Description")
                .available(false)
                .build();

        User finalAnotherUser = anotherUser;
        assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(finalAnotherUser.getId(), savedItemDto.getId(), updateDto);
        });
    }

    @Test
    void getItemByIdShouldThrowNotFoundExceptionWhenItemNotFound() {
        assertThrows(NotFoundException.class, () -> {
            itemService.getItemById(999L);
        });
    }

    @Test
    void createCommentShouldThrowValidationExceptionWhenUserNotFound() {
        ItemDto itemDto = ItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        ItemDto savedItemDto = itemService.addItem(testUser.getId(), itemDto);

        CommentDto commentDto = CommentDto.builder()
                .text("Test Comment")
                .build();

        assertThrows(ValidationException.class, () -> {
            itemService.createComment(999L, savedItemDto.getId(), commentDto);
        });
    }

    @Test
    void createCommentShouldThrowNotFoundExceptionWhenItemNotFound() {
        CommentDto commentDto = CommentDto.builder()
                .text("Test Comment")
                .build();

        assertThrows(NotFoundException.class, () -> {
            itemService.createComment(testUser.getId(), 999L, commentDto);
        });
    }

    @Test
    void createCommentShouldThrowValidationExceptionWhenUserDidNotBookItem() {
        ItemDto itemDto = ItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        ItemDto savedItemDto = itemService.addItem(testUser.getId(), itemDto);

        CommentDto commentDto = CommentDto.builder()
                .text("Test Comment")
                .build();

        assertThrows(ValidationException.class, () -> {
            itemService.createComment(testUser.getId(), savedItemDto.getId(), commentDto);
        });
    }

    @Test
    void addItemShouldAddItemWithRequestWhenRequestExists() {
        ItemDto itemDto = ItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .requestId(itemRequest.getId())
                .build();

        ItemDto savedItemDto = itemService.addItem(testUser.getId(), itemDto);

        assertNotNull(savedItemDto.getId());
        assertEquals(itemDto.getName(), savedItemDto.getName());
        assertEquals(itemDto.getDescription(), savedItemDto.getDescription());
        assertEquals(itemDto.getAvailable(), savedItemDto.getAvailable());
        assertEquals(itemRequest.getId(), savedItemDto.getRequestId());
    }

    @Test
    void addItemShouldAddItemWithoutRequestWhenRequestDoesNotExist() {
        ItemDto itemDto = ItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        ItemDto savedItemDto = itemService.addItem(testUser.getId(), itemDto);

        assertNotNull(savedItemDto.getId());
        assertEquals(itemDto.getName(), savedItemDto.getName());
        assertEquals(itemDto.getDescription(), savedItemDto.getDescription());
        assertEquals(itemDto.getAvailable(), savedItemDto.getAvailable());
        assertNull(savedItemDto.getRequestId());
    }

    @Test
    void updateItemShouldUpdateNameWhenNameIsProvided() {
        ItemDto itemDto = ItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        ItemDto savedItemDto = itemService.addItem(testUser.getId(), itemDto);

        ItemUpdateDto updateDto = ItemUpdateDto.builder()
                .name("Updated Name")
                .build();

        ItemDto updatedItemDto = itemService.updateItem(testUser.getId(), savedItemDto.getId(), updateDto);

        assertNotNull(updatedItemDto.getId());
        assertEquals(updateDto.getName(), updatedItemDto.getName());
        assertEquals(savedItemDto.getDescription(), updatedItemDto.getDescription());
        assertEquals(savedItemDto.getAvailable(), updatedItemDto.getAvailable());
    }

    @Test
    void updateItemShouldUpdateDescriptionWhenDescriptionIsProvided() {
        ItemDto itemDto = ItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        ItemDto savedItemDto = itemService.addItem(testUser.getId(), itemDto);

        ItemUpdateDto updateDto = ItemUpdateDto.builder()
                .description("Updated Description")
                .build();

        ItemDto updatedItemDto = itemService.updateItem(testUser.getId(), savedItemDto.getId(), updateDto);

        assertNotNull(updatedItemDto.getId());
        assertEquals(savedItemDto.getName(), updatedItemDto.getName());
        assertEquals(updateDto.getDescription(), updatedItemDto.getDescription());
        assertEquals(savedItemDto.getAvailable(), updatedItemDto.getAvailable());
    }

    @Test
    void updateItemShouldUpdateAvailableWhenAvailableIsProvided() {
        ItemDto itemDto = ItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        ItemDto savedItemDto = itemService.addItem(testUser.getId(), itemDto);

        ItemUpdateDto updateDto = ItemUpdateDto.builder()
                .available(false)
                .build();

        ItemDto updatedItemDto = itemService.updateItem(testUser.getId(), savedItemDto.getId(), updateDto);

        assertNotNull(updatedItemDto.getId());
        assertEquals(savedItemDto.getName(), updatedItemDto.getName());
        assertEquals(savedItemDto.getDescription(), updatedItemDto.getDescription());
        assertEquals(updateDto.getAvailable(), updatedItemDto.getAvailable());
    }

    @Test
    void searchItemsShouldReturnEmptyListWhenTextIsEmpty() {
        List<ItemDto> foundItems = itemService.searchItems("");

        assertNotNull(foundItems);
        assertTrue(foundItems.isEmpty());
    }

    @Test
    void searchItemsShouldReturnEmptyListWhenTextIsNull() {
        List<ItemDto> foundItems = itemService.searchItems(null);

        assertNotNull(foundItems);
        assertTrue(foundItems.isEmpty());
    }

    @Test
    void searchItemsShouldReturnItemsWhenTextIsNotEmpty() {
        itemService.addItem(testUser.getId(), itemDto1);
        itemService.addItem(testUser.getId(), itemDto2);

        List<ItemDto> foundItems = itemService.searchItems("Test");

        assertNotNull(foundItems);
        assertEquals(1, foundItems.size());
        assertEquals("Test Item 1", foundItems.getFirst().getName());
    }

    @Test
    void createCommentShouldThrowValidationExceptionWhenUserHasNotBookedItem() {
        CommentDto commentDto = CommentDto.builder()
                .text("Test Comment")
                .build();

        assertThrows(ValidationException.class, () -> {
            itemService.createComment(testUser.getId(), item.getId(), commentDto);
        });
    }
}