package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exceptions.UserNotFound;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.error.baseExceptions.ValidationError;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Получен список всех пользователей");
        return repository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(User user) {
        validateUser(user);
        log.info("Добавлен новый пользователь: " + user);
        return userMapper.toUserDto(repository.save(user));
    }

    @Override
    public UserDto updateUser(Long id, User user) {
        if (repository.findById(id).isEmpty()) {
            log.info(String.format("Пользователь с id = %d не найден", id));
            throw new UserNotFound("Пользователь с данным id не найден");
        }

        user.setId(id);

        User foundUser = repository.findById(id).get();

        if (user.getName() == null) {
            user.setName(foundUser.getName());
        }

        if (user.getEmail() == null) {
            user.setEmail(foundUser.getEmail());
        }

        if (!user.getEmail().equals(foundUser.getEmail())) {
            validateUser(user);
        }

        log.info("Обновлён пользователь: " + user);
        return userMapper.toUserDto(repository.save(user));
    }

    @Override
    public UserDto getUserById(Long id) {
        Optional<User> user = repository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFound("Пользователь с данным id не найден");
        }

        log.info("Получена информация о пользователе с id = " + id);
        return userMapper.toUserDto(user.get());
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Удалён пользователь с id = " + id);
        repository.deleteById(id);
    }

    private void validateUser(User user) {
        validateEmail(user.getEmail());
    }

    private void validateEmail(String email) {
        if (email == null) {
            throw new ValidationError("Невалидная почта");
        }

        if (!email.contains("@")) {
            throw new ValidationError("Невалидная почта");
        }

        String[] parts = email.split("@");
        if (!parts[1].contains(".")) {
            throw new ValidationError("Невалидная почта");
        }
    }
}
