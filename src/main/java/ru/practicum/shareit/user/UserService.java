package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserService {
    UserStorage userStorage;

    public UserDto createUser(UserDto userDto) {
        return UserMapper.toUserDto(userStorage.createUser(UserMapper.toUser(userDto)));
    }

    public UserDto updateUser(UserDto userDto, Integer id) {
        if (userDto.getId() == null) {
            userDto.setId(id);
        }

        return UserMapper.toUserDto((userStorage.updateUser(UserMapper.toUser(userDto))));
    }

    public UserDto deleteUser(Integer id) {
        return UserMapper.toUserDto(userStorage.deleteUser(id));
    }

    public UserDto getUserById(Integer id) {
        return UserMapper.toUserDto(userStorage.getUserById(id));
    }

    public List<UserDto> getUsers() {
        return userStorage.getUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(toList());
    }
}
