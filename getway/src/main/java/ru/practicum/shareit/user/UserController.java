package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto user) {
        return userClient.createUser(user);
    }

    @PatchMapping("/{user-id}")
    public ResponseEntity<Object> updateUser(@PathVariable("user-id") long userId,
                                             @Valid @RequestBody UserUpdateDto user) {
        return userClient.update(userId, user);
    }

    @GetMapping("/{user-id}")
    public ResponseEntity<Object> getUserById(@PathVariable("user-id") long userId) {
        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        return userClient.getUsers();
    }

    @DeleteMapping("/{user-id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("user-id") long userId) {
        return userClient.deleteUser(userId);
    }
}
