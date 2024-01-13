package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryJpaTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    private Item item;
    private User owner;

    PageRequest page = PageRequest.of(0, 10);

    @BeforeEach
    void saveData() {
        owner = new User();
        owner.setName("Admin admin");
        owner.setEmail("admin@shareit.ru");
        em.persist(owner);

        item = new Item();
        item.setName("Телескоп");
        item.setDescription("Очень мощный телескоп");
        item.setOwner(owner);
        item.setAvailable(true);
        em.persist(item);

        em.flush();
    }

    @Test
    void test_findByOwnerId() {
        Page<Item> resultItems = itemRepository.findByOwnerId(
                owner.getId(),
                page
        );
        assertEquals(1, resultItems.toList().size());
    }

    @Test
    void test_search() {
        Page<Item> resultItems = itemRepository.search(
                "тел",
                page
        );
        assertEquals(1, resultItems.toList().size());
    }

    @Test
    void test_findAllByRequestId() {
        User requestor = new User();
        requestor.setName("Test test");
        requestor.setEmail("test@shareit.ru");
        em.persist(requestor);

        ItemRequest req = new ItemRequest();
        req.setDescription("Очень срочно нужна лупа");
        req.setRequestor(requestor);
        req.setCreated(LocalDateTime.now());
        em.persist(req);

        Item item = new Item();
        item.setName("Лупа");
        item.setDescription("Очень мощная лупа");
        item.setOwner(owner);
        item.setAvailable(true);
        item.setRequestId(req.getId());
        em.persist(item);

        em.flush();

        List<Item> resultItems = itemRepository
                .findAllByRequestId(req.getId(), Sort.by("id"));
        assertEquals(1, resultItems.size());
    }

    @AfterEach
    void deleteData() {
        itemRepository.deleteAll();
    }
}
