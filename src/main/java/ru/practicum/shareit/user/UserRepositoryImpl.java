package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private Long currentId = 0L;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findById(Long id) {
        if (users.containsKey(id)) {
            return Optional.of(users.get(id));
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        List<User> found = users.values()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .collect(Collectors.toList());

        if (found.isEmpty()) {
            return  Optional.empty();
        }

        return Optional.of(found.get(0));
    }

    @Override
    public User create(User user) {
        Long id = getId();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public User update(Long id, User user) {
        user.setId(id);

        if (user.getName() != null) {
            users.get(id).setName(user.getName());
        }

        if (user.getEmail() != null) {
            users.get(id).setEmail(user.getEmail());
        }

        return users.get(id);
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    private Long getId() {
        return ++currentId;
    }
}
