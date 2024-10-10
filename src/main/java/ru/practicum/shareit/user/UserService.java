package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserService {
    UserStorage userStorage;
    UserMapper userMapper;

    public UserDto createUser(UserDto userDto) {
        return userMapper.toUserDto(userStorage.createUser(userMapper.toUser(userDto)));
    }

    public UserDto updateUser(UserDto userDto, Integer id) {
        if (userDto.getId() == null) {
            userDto.setId(id);
        }

        return userMapper.toUserDto((userStorage.updateUser(userMapper.toUser(userDto))));
    }

    public UserDto deleteUser(Integer id) {
        return userMapper.toUserDto(userStorage.deleteUser(id));
    }

    public UserDto getUserById(Integer id) {
        return userMapper.toUserDto(userStorage.getUserById(id));
    }

    public List<UserDto> getUsers() {
        return userStorage.getUsers().stream()
                .map(userMapper::toUserDto)
                .collect(toList());
    }
}
