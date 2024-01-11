package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.exceptions.ItemNotFound;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @Mock
    private ItemRepository mockItemRepository;

    @Test
    void shouldThrowExceptionWhenGetItemWithWrongId() {
        ItemService itemService = new ItemServiceImpl(null, null, null, mockItemRepository, null, null);

        when(mockItemRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        final ItemNotFound exception = Assertions.assertThrows(
                ItemNotFound.class,
                () -> itemService.getItemById(-1L, 1L)
        );

        Assertions.assertEquals("Вещь с данным id не найдена", exception.getMessage());
    }
}
