package ru.practicum.shareit;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ItemController itemController;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testAddItem() throws Exception {
        Long userId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        when(itemService.addItem(userId, itemDto)).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    public void testUpdateItem() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .name("Updated Item")
                .description("Updated Description")
                .available(false)
                .build();

        ItemDto updatedItemDto = ItemDto.builder()
                .name("Updated Item")
                .description("Updated Description")
                .available(false)
                .build();

        when(itemService.updateItem(userId, itemId, itemUpdateDto)).thenReturn(updatedItemDto);

        mockMvc.perform(patch("/items/{item-id}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Item"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    public void testGetItemById() throws Exception {
        Long itemId = 1L;
        ItemCommentDto itemCommentDto = ItemCommentDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .comments(Collections.singletonList(CommentDto.builder()
                        .text("Test Comment")
                        .authorName("Test Author")
                        .created(LocalDateTime.now())
                        .build()))
                .build();

        when(itemService.getItemById(itemId)).thenReturn(itemCommentDto);

        mockMvc.perform(get("/items/{item-id}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.comments[0].text").value("Test Comment"))
                .andExpect(jsonPath("$.comments[0].authorName").value("Test Author"));
    }

    @Test
    public void testGetItemsByOwnerId() throws Exception {
        Long userId = 1L;
        ItemDto itemDto1 = ItemDto.builder()
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .build();
        ItemDto itemDto2 = ItemDto.builder()
                .name("Item 2")
                .description("Description 2")
                .available(false)
                .build();

        List<ItemDto> items = Arrays.asList(itemDto1, itemDto2);

        when(itemService.getItemsByOwnerId(userId)).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Item 1"))
                .andExpect(jsonPath("$[0].description").value("Description 1"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[1].name").value("Item 2"))
                .andExpect(jsonPath("$[1].description").value("Description 2"))
                .andExpect(jsonPath("$[1].available").value(false));
    }

    @Test
    public void testGetItemsBySearchQuery() throws Exception {
        String text = "search";
        ItemDto itemDto1 = ItemDto.builder()
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .build();
        ItemDto itemDto2 = ItemDto.builder()
                .name("Item 2")
                .description("Description 2")
                .available(false)
                .build();

        List<ItemDto> items = Arrays.asList(itemDto1, itemDto2);

        when(itemService.searchItems(text)).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", text))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Item 1"))
                .andExpect(jsonPath("$[0].description").value("Description 1"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[1].name").value("Item 2"))
                .andExpect(jsonPath("$[1].description").value("Description 2"))
                .andExpect(jsonPath("$[1].available").value(false));
    }

    @Test
    public void testCreateComment() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        CommentDto commentDto = CommentDto.builder()
                .text("Test Comment")
                .authorName("Test Author")
                .created(LocalDateTime.now())
                .build();

        when(itemService.createComment(userId, itemId, commentDto)).thenReturn(commentDto);

        mockMvc.perform(post("/items/{item-id}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Test Comment"))
                .andExpect(jsonPath("$.authorName").value("Test Author"));
    }
}
