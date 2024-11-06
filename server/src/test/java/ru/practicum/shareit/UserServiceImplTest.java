package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserAlreadyExistsException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DataJpaTest
public class UserServiceImplTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    public void testCreateUser() {
        UserDto userDto = UserDto.builder()
                .name("Test User")
                .email("test@example.com")
                .build();

        UserDto createdUser = userService.createUser(userDto);

        assertNotNull(createdUser.getId());
        assertEquals(userDto.getName(), createdUser.getName());
        assertEquals(userDto.getEmail(), createdUser.getEmail());
    }

    @Test
    public void testCreateUserWithExistingEmail() {
        UserDto userDto = UserDto.builder()
                .name("Test User")
                .email("test@example.com")
                .build();

        userService.createUser(userDto);

        UserDto userDto2 = UserDto.builder()
                .name("Another User")
                .email("test@example.com")
                .build();

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(userDto2));
    }

    @Test
    public void testUpdateUser() {
        UserDto userDto = UserDto.builder()
                .name("Test User")
                .email("test@example.com")
                .build();

        UserDto createdUser = userService.createUser(userDto);

        UserDto updatedUserDto = UserDto.builder()
                .name("Updated User")
                .email("updated@example.com")
                .build();

        UserDto updatedUser = userService.updateUser(updatedUserDto, createdUser.getId());

        assertEquals(updatedUserDto.getName(), updatedUser.getName());
        assertEquals(updatedUserDto.getEmail(), updatedUser.getEmail());
    }

    @Test
    public void testDeleteUser() {
        UserDto userDto = UserDto.builder()
                .name("Test User")
                .email("test@example.com")
                .build();

        UserDto createdUser = userService.createUser(userDto);

        userService.deleteUser(createdUser.getId());

        assertThrows(NotFoundException.class, () -> userService.getUserById(createdUser.getId()));
    }

    @Test
    public void testGetUserById() {
        UserDto userDto = UserDto.builder()
                .name("Test User")
                .email("test@example.com")
                .build();

        UserDto createdUser = userService.createUser(userDto);

        UserDto fetchedUser = userService.getUserById(createdUser.getId());

        assertEquals(createdUser.getId(), fetchedUser.getId());
        assertEquals(createdUser.getName(), fetchedUser.getName());
        assertEquals(createdUser.getEmail(), fetchedUser.getEmail());
    }

    @Test
    public void testGetUsers() {
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

        assertEquals(2, userService.getUsers().size());
    }
}
