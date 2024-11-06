package ru.practicum.shareit;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ItemServiceImplTest {
    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User testUser;

    @BeforeEach
    public void setUp() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        testUser = User.builder()
                .name("Test User")
                .email("test@example.com")
                .build();
        testUser = userRepository.save(testUser);
    }

    @Test
    public void testAddItem() {
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

        Item savedItem = itemRepository.findById(savedItemDto.getId()).orElse(null);
        assertNotNull(savedItem);
        assertEquals(savedItemDto.getName(), savedItem.getName());
        assertEquals(savedItemDto.getDescription(), savedItem.getDescription());
        assertEquals(savedItemDto.getAvailable(), savedItem.getAvailable());
    }

    @Test
    public void testAddItemWithInvalidData() {
        ItemDto itemDto = ItemDto.builder()
                .name("")
                .description("Test Description")
                .available(true)
                .build();

        assertThrows(ValidationException.class, () -> itemService.addItem(testUser.getId(), itemDto));
    }

    @Test
    public void testAddItemWithNonExistentUser() {
        ItemDto itemDto = ItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        assertThrows(NotFoundException.class, () -> itemService.addItem(999L, itemDto));
    }
}
