package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestThingsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestServiceImpl itemRequestService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ItemRequestRepository itemRequestRepository;

    @MockBean
    private ItemRepository itemRepository;

    @Test
    public void testAddRequest() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Test request");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenAnswer(i -> i.getArguments()[0]);

        ItemRequestDto result = itemRequestService.addRequest(userId, itemRequestDto);

        assertNotNull(result.getCreated());
        assertEquals(user, result.getRequester());
        assertEquals("Test request", result.getDescription());
    }

    @Test
    public void testGetAllRequests() {
        Long userId = 1L;
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setRequester(new User());
        itemRequest.setCreated(LocalDateTime.now());

        when(itemRequestRepository.findByRequester_Id(userId)).thenReturn(List.of(itemRequest));
        when(itemRepository.findItemsByRequestIds(anyList())).thenReturn(List.of());

        List<ItemRequestThingsDto> result = itemRequestService.getAllRequests(userId);

        assertEquals(1, result.size());
        assertEquals(itemRequest.getId(), result.getFirst().getId());
    }
}