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
        return userService.createUser(userDto);
    }

    @ResponseBody
    @PatchMapping("/{user-id}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable("user-id") Integer userId) {
        return userService.updateUser(userDto, userId);
    }

    @DeleteMapping("/{user-id}")
    public UserDto deleteUser(@PathVariable("user-id") Integer userId) {
        return userService.deleteUser(userId);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{user-id}")
    public UserDto getUserById(@PathVariable("user-id") Integer userId) {
        return userService.getUserById(userId);
    }
}
