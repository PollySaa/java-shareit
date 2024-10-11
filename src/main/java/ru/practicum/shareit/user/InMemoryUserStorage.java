package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserAlreadyExistsException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("InMemoryUserStorage")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InMemoryUserStorage implements UserStorage {
    final Map<Integer, User> users = new HashMap<>();
    Integer generateId = 1;

    @Override
    public User createUser(User user) {
        if (user.getEmail() == null) {
            throw new ValidationException("Передан пустой аргумент!");
        }

        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new UserAlreadyExistsException("Пользователь с email = " + user.getEmail() + " уже существует!");
        }

        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Некорректный email пользователя: " + user.getEmail());
        }

        user.setId(generateId++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        validation(user.getId());
        if (user.getName() == null) {
            user.setName(users.get(user.getId()).getName());
        }

        if (user.getEmail() == null) {
            user.setEmail(users.get(user.getId()).getEmail());
        }

        if (users.values().stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .allMatch(u -> u.getId().equals(user.getId()))) {
            users.put(user.getId(), user);
        } else {
            throw new UserAlreadyExistsException("Пользователь с email = " + user.getEmail() + " уже существует!");
        }
        return user;
    }

    @Override
    public User deleteUser(Integer id) {
        validation(id);
        return users.remove(id);
    }

    @Override
    public User getUserById(Integer id) {
        validation(id);
        return users.get(id);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());

    }

    private void validation(Integer id) {
        if (id == null) {
            throw new ValidationException("Передан пустой аргумент!");
        }

        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с таким id = " + id + "не был найден!");
        }
    }
}
