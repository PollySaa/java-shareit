package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class RequestDtoTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSerializeDeserializeItemRequestDto() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Test description");
        itemRequestDto.setCreated("2023-10-01T12:00:00");

        String json = objectMapper.writeValueAsString(itemRequestDto);
        ItemRequestDto deserializedDto = objectMapper.readValue(json, ItemRequestDto.class);

        assertEquals(itemRequestDto.getDescription(), deserializedDto.getDescription());
        assertEquals(itemRequestDto.getCreated(), deserializedDto.getCreated());
    }
}
