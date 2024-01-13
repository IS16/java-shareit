package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto createUser(UserDto user);

    UserDto updateUser(Long id, UserDto user);

    UserDto getUserById(Long id);

    void deleteUser(Long id);
}
