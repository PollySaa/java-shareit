package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    User deleteUser(Integer id);

    User getUserById(Integer id);

    List<User> getUsers();
}
