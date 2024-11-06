package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping(path = "/users")
public class UserController {
    UserService userService;

    @ResponseBody
    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        log.info("Выполнение createUser");
        return userService.createUser(userDto);
    }

    @ResponseBody
    @PatchMapping("/{user-id}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable("user-id") Long userId) {
        log.info("Выполнение updateUser");
        return userService.updateUser(userDto, userId);
    }

    @DeleteMapping("/{user-id}")
    public void deleteUser(@PathVariable("user-id") Long userId) {
        log.info("Выполнение deleteUser");
        userService.deleteUser(userId);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("Выполнение getUsers");
        return userService.getUsers();
    }

    @GetMapping("/{user-id}")
    public UserDto getUserById(@PathVariable("user-id") Long userId) {
        log.info("Выполнение getUserById");
        return userService.getUserById(userId);
    }
}
