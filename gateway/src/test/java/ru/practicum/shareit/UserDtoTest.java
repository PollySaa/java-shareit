package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class UserDtoTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSerializeDeserializeUserDto() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");

        String json = objectMapper.writeValueAsString(userDto);
        UserDto deserializedDto = objectMapper.readValue(json, UserDto.class);

        assertEquals(userDto.getName(), deserializedDto.getName());
        assertEquals(userDto.getEmail(), deserializedDto.getEmail());
    }

    @Test
    public void testSerializeDeserializeUserUpdateDto() throws Exception {
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setName("Updated User");
        userUpdateDto.setEmail("updated@example.com");

        String json = objectMapper.writeValueAsString(userUpdateDto);
        UserUpdateDto deserializedDto = objectMapper.readValue(json, UserUpdateDto.class);

        assertEquals(userUpdateDto.getName(), deserializedDto.getName());
        assertEquals(userUpdateDto.getEmail(), deserializedDto.getEmail());
    }
}
