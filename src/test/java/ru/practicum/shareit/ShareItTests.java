package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserAlreadyExistsException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.InMemoryUserStorage;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ShareItTests {
    private UserService userService;
	private UserDto userDto1;
	private UserDto userDto2;
	@Autowired
	ItemService itemService;

	@BeforeEach
	void setUp() {
        UserStorage userStorage = new InMemoryUserStorage();
        UserMapper userMapper = new UserMapper();
		userService = new UserService(userStorage, userMapper);
		userDto1 = new UserDto(1, "Иван", "ivan@mail.ru");
		userDto2 = new UserDto(2, "Петр", "petr@mail.ru");
	}

	@Test
	void createUser() {
		UserDto createdUserDto = userService.createUser(userDto1);

		assertNotNull(createdUserDto.getId());
		assertEquals("Иван", createdUserDto.getName());
		assertEquals("ivan@mail.ru", createdUserDto.getEmail());
	}

	@Test
	void updateUser_withExistingEmail() {
		UserDto createdUserDto1 = userService.createUser(userDto1);
		UserDto createdUserDto2 = userService.createUser(userDto2);

		UserDto userDto3 = new UserDto(createdUserDto1.getId(), "Сидор", "petr@mail.ru");

		assertThrows(UserAlreadyExistsException.class, () -> userService.updateUser(userDto3, createdUserDto1.getId()));
	}

	@Test
	void deleteUser() {
		UserDto createdUserDto = userService.createUser(userDto1);

		UserDto deletedUserDto = userService.deleteUser(createdUserDto.getId());

		assertEquals(createdUserDto.getId(), deletedUserDto.getId());
		assertEquals("Иван", deletedUserDto.getName());
		assertEquals("ivan@mail.ru", deletedUserDto.getEmail());
	}

	@Test
	void deleteUser_notFound() {
		assertThrows(NotFoundException.class, () -> userService.deleteUser(1));
	}

	@Test
	void getUserById() {
		UserDto createdUserDto = userService.createUser(userDto1);

		UserDto fetchedUserDto = userService.getUserById(createdUserDto.getId());

		assertEquals(createdUserDto.getId(), fetchedUserDto.getId());
		assertEquals("Иван", fetchedUserDto.getName());
		assertEquals("ivan@mail.ru", fetchedUserDto.getEmail());
	}

	@Test
	void getUserById_notFound() {
		assertThrows(NotFoundException.class, () -> userService.getUserById(1));
	}

	@Test
	void getUsers() {
		userService.createUser(userDto1);
		userService.createUser(userDto2);

		List<UserDto> users = userService.getUsers();

		assertEquals(2, users.size());
	}

	@Test
	void updateItemNotFoundException() {
		ItemDto itemDto = new ItemDto(2, "Book", "Some description", true, null);
		assertThrows(NotFoundException.class, () -> itemService.updateItem(2, 1, itemDto));
	}
}
