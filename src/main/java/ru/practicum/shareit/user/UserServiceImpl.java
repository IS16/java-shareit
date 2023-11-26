package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.exceptions.UserAlreadyExists;
import ru.practicum.shareit.user.exceptions.UserNotFound;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.error.baseExceptions.ValidationError;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public User createUser(User user) {
        validateUser(user);

        return repository.create(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        if (repository.findById(id).isEmpty()) {
            throw new UserNotFound("Пользователь с данным id не найден");
        }

        User foundUser = repository.findById(id).get();

        if (foundUser.getEmail().equals(user.getEmail())) {
            user.setEmail(null);
        }

        if (user.getEmail() != null) {
            validateUser(user);
        }

        return repository.update(id, user);
    }

    @Override
    public User getUserById(Long id) {
        Optional<User> user = repository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFound("Пользователь с данным id не найден");
        }

        return user.get();
    }

    @Override
    public void deleteUser(Long id) {
        repository.delete(id);
    }

    private void validateUser(User user) {
        validateEmail(user.getEmail());
        checkEmailExists(user);
    }

    private void checkEmailExists(User user) {
        if (repository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExists("Пользователь с данной почтой уже существует");
        }
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
