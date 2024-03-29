package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends PagingAndSortingRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestorId(Long requestorId, Sort sort);

    Page<ItemRequest> findAllByRequestorIdNot(Long userId, Pageable pageable);

    List<ItemRequest> findAllByRequestorIdNotOrderByCreatedDesc(Long userId);
}
