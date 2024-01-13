package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestDto itemRequestDto, Long requestorId);

    ItemRequestDto getItemRequestById(Long itemRequestId, Long requestorId);

    List<ItemRequestDto> getOwnItemRequests(Long requestorId);

    List<ItemRequestDto> getAllItemRequests(Long requestorId, Integer from, Integer size);
}
