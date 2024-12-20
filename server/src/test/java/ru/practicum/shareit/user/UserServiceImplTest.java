package ru.practicum.shareit.user;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserAlreadyExistsException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserServiceImplTest {
    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testCreateUser() {
        UserDto userDto = UserDto.builder()
                .name("Test User")
                .email("test@example.com")
                .build();

        UserDto createdUserDto = userService.createUser(userDto);

        assertNotNull(createdUserDto.getId());
        assertEquals(userDto.getName(), createdUserDto.getName());
        assertEquals(userDto.getEmail(), createdUserDto.getEmail());
    }

    @Test
    void testUpdateUser() {
        UserDto userDto = UserDto.builder()
                .name("Test User")
                .email("test@example.com")
                .build();

        UserDto createdUserDto = userService.createUser(userDto);

        UserDto updatedUserDto = UserDto.builder()
                .name("Updated User")
                .email("updated@example.com")
                .build();

        UserDto updatedUser = userService.updateUser(updatedUserDto, createdUserDto.getId());

        assertNotNull(updatedUser.getId());
        assertEquals(updatedUserDto.getName(), updatedUser.getName());
        assertEquals(updatedUserDto.getEmail(), updatedUser.getEmail());
    }

    @Test
    void testDeleteUser() {
        UserDto userDto = UserDto.builder()
                .name("Test User")
                .email("test@example.com")
                .build();

        UserDto createdUserDto = userService.createUser(userDto);

        userService.deleteUser(createdUserDto.getId());

        assertThrows(NotFoundException.class, () -> userService.getUserById(createdUserDto.getId()));
    }

    @Test
    void testGetUserById() {
        UserDto userDto = UserDto.builder()
                .name("Test User")
                .email("test@example.com")
                .build();

        UserDto createdUserDto = userService.createUser(userDto);

        UserDto retrievedUserDto = userService.getUserById(createdUserDto.getId());

        assertNotNull(retrievedUserDto);
        assertEquals(createdUserDto.getId(), retrievedUserDto.getId());
        assertEquals(createdUserDto.getName(), retrievedUserDto.getName());
        assertEquals(createdUserDto.getEmail(), retrievedUserDto.getEmail());
    }

    @Test
    void testGetUsers() {
        UserDto userDto1 = UserDto.builder()
                .name("Test User 1")
                .email("test1@example.com")
                .build();

        UserDto userDto2 = UserDto.builder()
                .name("Test User 2")
                .email("test2@example.com")
                .build();

        userService.createUser(userDto1);
        userService.createUser(userDto2);

        List<UserDto> users = userService.getUsers();

        assertNotNull(users);
        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getName().equals("Test User 1")));
        assertTrue(users.stream().anyMatch(u -> u.getName().equals("Test User 2")));
    }

    @Test
    void createUserShouldThrowUserAlreadyExistsExceptionWhenEmailAlreadyExists() {
        UserDto userDto1 = UserDto.builder()
                .name("Test User 1")
                .email("test@example.com")
                .build();

        userService.createUser(userDto1);

        UserDto userDto2 = UserDto.builder()
                .name("Test User 2")
                .email("test@example.com")
                .build();

        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.createUser(userDto2);
        });
    }

    @Test
    void updateUserShouldThrowNotFoundExceptionWhenUserNotFound() {
        UserDto userDto = UserDto.builder()
                .name("Updated User")
                .email("updated@example.com")
                .build();

        assertThrows(NotFoundException.class, () -> {
            userService.updateUser(userDto, 999L);
        });
    }

    @Test
    void getUserByIdShouldThrowNotFoundExceptionWhenUserNotFound() {
        assertThrows(NotFoundException.class, () -> {
            userService.getUserById(999L);
        });
    }

    @Test
    void updateUserShouldUpdateEmailWhenEmailIsProvided() {
        UserDto userDto = UserDto.builder()
                .name("Test User")
                .email("test@example.com")
                .build();

        UserDto createdUserDto = userService.createUser(userDto);

        UserDto updateDto = UserDto.builder()
                .email("updated@example.com")
                .build();

        UserDto updatedUserDto = userService.updateUser(updateDto, createdUserDto.getId());

        assertNotNull(updatedUserDto.getId());
        assertEquals(createdUserDto.getName(), updatedUserDto.getName());
        assertEquals(updateDto.getEmail(), updatedUserDto.getEmail());
    }

    @Test
    void testDeleteUserById() {
        assertThrows(NotFoundException.class, () -> {
            userService.deleteUser(999L);
        });
    }
}
