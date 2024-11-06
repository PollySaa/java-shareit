package ru.practicum.shareit;

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
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
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

    private User testUser;
    private ItemDto itemDto;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        testUser = User.builder()
                .name("Test User")
                .email("test@example.com")
                .build();
        testUser = userRepository.save(testUser);

        itemDto = ItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();
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
    void testGetItemsByOwnerId() {
        ItemDto itemDto1 = ItemDto.builder()
                .name("Test Item 1")
                .description("Test Description 1")
                .available(true)
                .build();

        ItemDto itemDto2 = ItemDto.builder()
                .name("Test Item 2")
                .description("Test Description 2")
                .available(true)
                .build();

        itemService.addItem(testUser.getId(), itemDto1);
        itemService.addItem(testUser.getId(), itemDto2);

        List<ItemDto> items = itemService.getItemsByOwnerId(testUser.getId());

        assertNotNull(items);
        assertEquals(2, items.size());
        assertEquals("Test Item 1", items.get(0).getName());
        assertEquals("Test Item 2", items.get(1).getName());
    }

    @Test
    void testSearchItems() {
        ItemDto itemDto1 = ItemDto.builder()
                .name("Test Item 1")
                .description("Test Description 1")
                .available(true)
                .build();

        ItemDto itemDto2 = ItemDto.builder()
                .name("Test Item 2")
                .description("Test Description 2")
                .available(false)
                .build();

        itemService.addItem(testUser.getId(), itemDto1);
        itemService.addItem(testUser.getId(), itemDto2);

        List<ItemDto> foundItems = itemService.searchItems("Test");

        assertNotNull(foundItems);
        assertEquals(1, foundItems.size());
        assertEquals("Test Item 1", foundItems.getFirst().getName());
    }

    @Test
    void testCreateComment() {
        Item item = Item.builder()
                .owner(testUser)
                .name("Test Item")
                .description("Test Description")
                .available(false)
                .build();
        itemRepository.save(item);

        Booking booking = Booking.builder()
                .status(Status.APPROVED)
                .item(item)
                .booker(testUser)
                .start(LocalDateTime.now().plusMinutes(5))
                .end(LocalDateTime.now().plusMinutes(10))
                .build();
        bookingRepository.save(booking);

        Comment comment = Comment.builder()
                .text("Test Comment")
                .item(item)
                .author(testUser)
                .createdDate(LocalDateTime.now())
                .build();

        Comment newComment = commentRepository.save(comment);
        assertEquals(comment.getText(), newComment.getText());
        assertEquals(comment.getAuthor(), newComment.getAuthor());
        assertEquals(comment.getCreatedDate(), newComment.getCreatedDate());
        assertEquals(comment.getItem(), newComment.getItem());
        assertEquals(comment.getAuthor(), newComment.getAuthor());
        assertEquals(comment.getCreatedDate(), newComment.getCreatedDate());
    }

}
