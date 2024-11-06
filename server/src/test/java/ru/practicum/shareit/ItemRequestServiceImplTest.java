package ru.practicum.shareit;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestThingsDto;
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
public class ItemRequestServiceImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository requestRepository;

    @Autowired
    private ItemRequestService itemRequestService;

    private User user;

    @BeforeEach
    void setUp() {
        requestRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        user = User.builder()
                .name("Test User")
                .email("test@test.com")
                .build();
        user = userRepository.save(user);
    }

    @Test
    void testAddRequest() {
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("Test Request Description")
                .build();

        ItemRequestDto savedRequestDto = itemRequestService.addRequest(user.getId(), requestDto);

        assertNotNull(savedRequestDto.getId());
        assertEquals(requestDto.getDescription(), savedRequestDto.getDescription());
        assertEquals(user.getId(), savedRequestDto.getRequester().getId());
        assertNotNull(savedRequestDto.getCreated());
    }

    @Test
    void testGetRequest() {
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("Test Request Description")
                .requester(user)
                .created(LocalDateTime.now())
                .build();

        ItemRequest savedRequest = requestRepository.save(ItemRequestMapper.toItemRequest(requestDto));

        ItemRequestThingsDto retrievedRequest = itemRequestService.get(savedRequest.getId());

        assertNotNull(retrievedRequest);
        assertEquals(savedRequest.getId(), retrievedRequest.getId());
        assertEquals(savedRequest.getDescription(), retrievedRequest.getDescription());
        assertNotNull(retrievedRequest.getCreated());
        assertTrue(retrievedRequest.getItems().isEmpty());
    }

    @Test
    void testGetAllRequests() {
        ItemRequestDto requestDto1 = ItemRequestDto.builder()
                .description("Test Request Description 1")
                .requester(user)
                .created(LocalDateTime.now())
                .build();

        ItemRequestDto requestDto2 = ItemRequestDto.builder()
                .description("Test Request Description 2")
                .requester(user)
                .created(LocalDateTime.now().minusHours(1))
                .build();

        requestRepository.save(ItemRequestMapper.toItemRequest(requestDto1));
        requestRepository.save(ItemRequestMapper.toItemRequest(requestDto2));

        List<ItemRequestThingsDto> allRequests = itemRequestService.getAllRequests(user.getId());

        assertNotNull(allRequests);
        assertEquals(2, allRequests.size());
        assertEquals("Test Request Description 1", allRequests.get(0).getDescription());
        assertEquals("Test Request Description 2", allRequests.get(1).getDescription());
    }

    @Test
    void testGetAllByUser() {
        User anotherUser = User.builder()
                .name("Another User")
                .email("another@test.com")
                .build();
        anotherUser = userRepository.save(anotherUser);

        ItemRequestDto requestDto1 = ItemRequestDto.builder()
                .description("Test Request Description 1")
                .requester(anotherUser)
                .created(LocalDateTime.now())
                .build();

        ItemRequestDto requestDto2 = ItemRequestDto.builder()
                .description("Test Request Description 2")
                .requester(anotherUser)
                .created(LocalDateTime.now().minusHours(1))
                .build();

        requestRepository.save(ItemRequestMapper.toItemRequest(requestDto1));
        requestRepository.save(ItemRequestMapper.toItemRequest(requestDto2));

        List<ItemRequestDto> allRequests = itemRequestService.getAllByUser(user.getId());

        assertNotNull(allRequests);
        assertEquals(2, allRequests.size());
        assertEquals("Test Request Description 1", allRequests.get(0).getDescription());
        assertEquals("Test Request Description 2", allRequests.get(1).getDescription());
    }
}