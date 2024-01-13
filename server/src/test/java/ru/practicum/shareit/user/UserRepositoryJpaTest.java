package ru.practicum.shareit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class UserRepositoryJpaTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void saveData() {
        User user = new User();
        user.setName("Admin");
        user.setEmail("admin@shareit.ru");
        em.persist(user);

        em.flush();
    }

    @Test
    void test_findByEmail() {
        List<User> resultUsers = userRepository.findByEmail("admin@shareit.ru");
        assertEquals(1, resultUsers.size());
    }

    @AfterEach
    void deleteData() {
        userRepository.deleteAll();
    }
}
