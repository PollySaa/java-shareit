package ru.practicum.shareit;

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
public class ItemServiceImplTest {
    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void testAddItem() {
        User user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .build();
        user = userRepository.save(user);

        ItemDto itemDto = ItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        ItemDto savedItemDto = itemService.addItem(user.getId(), itemDto);

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
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .build();
        user = userRepository.save(user);

        ItemDto itemDto = ItemDto.builder()
                .name("")
                .description("Test Description")
                .available(true)
                .build();

        assertThrows(ValidationException.class, () -> itemService.addItem(1L, itemDto));
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
