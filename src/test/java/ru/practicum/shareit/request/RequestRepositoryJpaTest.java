package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class RequestRepositoryJpaTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User requestor;

    PageRequest page = PageRequest.of(0, 10);

    @BeforeEach
    void saveData() {
        requestor = new User();
        requestor.setName("Admin");
        requestor.setEmail("admin@shareit.ru");
        em.persist(requestor);

        ItemRequest req = new ItemRequest();
        req.setDescription("Очень срочно нужна лупа");
        req.setRequestor(requestor);
        req.setCreated(LocalDateTime.now());
        em.persist(req);

        em.flush();
    }

    @Test
    void test_findAllByRequestorId() {
        List<ItemRequest> resultRequests = itemRequestRepository.findAllByRequestorId(
                requestor.getId(),
                Sort.by("id")
        );
        assertEquals(1, resultRequests.size());
    }

    @Test
    void test_findAllByRequestorIdNot() {
        Page<ItemRequest> resultRequests = itemRequestRepository.findAllByRequestorIdNot(
                requestor.getId(),
                page
        );
        assertEquals(0, resultRequests.toList().size());
    }

    @AfterEach
    void deleteData() {
        itemRequestRepository.deleteAll();
    }
}
