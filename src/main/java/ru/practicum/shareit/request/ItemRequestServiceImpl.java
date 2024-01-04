package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exceptions.ItemRequestNotFound;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.service.MapperService;
import ru.practicum.shareit.service.ValidationService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.utils.Pagination;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final MapperService mapperService;
    private final ValidationService validationService;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemService itemService;

    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long requestorId) {
        UserDto user = validationService.getUserById(requestorId);

        itemRequestDto.setRequestor(user);
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest req = itemRequestRepository.save(mapperService.toItemRequest(itemRequestDto));
        itemRequestDto.setId(req.getId());
        return itemRequestDto;
    }

    @Override
    public ItemRequestDto getItemRequestById(Long itemRequestId, Long requestorId) {
        validationService.getUserById(requestorId);

        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new ItemRequestNotFound(String.format("Запрос с id = %d не найден", itemRequestId)));


        List<ItemDto> itemDtos = itemService.getItemsByRequestId(itemRequest.getId());
        return mapperService.toItemRequestDto(itemRequest, itemDtos);
    }

    @Override
    public List<ItemRequestDto> getOwnItemRequests(Long requestorId) {
        validationService.getUserById(requestorId);

        return itemRequestRepository.findAllByRequestorId(requestorId, Sort.by(Sort.Direction.DESC, "created"))
                .stream()
                .map(itemRequest -> {
                    List<ItemDto> itemDtos = itemService.getItemsByRequestId(itemRequest.getId());
                    return mapperService.toItemRequestDto(itemRequest, itemDtos);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Long requestorId, Integer from, Integer size) {
        validationService.getUserById(requestorId);

        List<ItemRequestDto> listItemRequestDto = new ArrayList<>();

        if (size == null) {
            List<ItemRequest> listItemRequest = itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(requestorId);
            listItemRequestDto.addAll(listItemRequest.stream().skip(from).map(itemRequest -> {
                List<ItemDto> itemDtos = itemService.getItemsByRequestId(itemRequest.getId());
                return mapperService.toItemRequestDto(itemRequest, itemDtos);
            }).collect(Collectors.toList()));
        } else {
            Pageable pageable;
            Page<ItemRequest> page;
            Pagination pager = new Pagination(from, size);
            Sort sort = Sort.by(Sort.Direction.DESC, "created");

            for (int i = pager.getPageStart(); i < pager.getPagesAmount(); i++) {
                pageable = PageRequest.of(i, pager.getPageSize(), sort);

                page = itemRequestRepository.findAllByRequestorIdNot(requestorId, pageable);
                listItemRequestDto.addAll(page.stream().map(itemRequest -> {
                    List<ItemDto> itemDtos = itemService.getItemsByRequestId(itemRequest.getId());
                    return mapperService.toItemRequestDto(itemRequest, itemDtos);
                }).collect(Collectors.toList()));
            }

            listItemRequestDto = listItemRequestDto.stream().limit(size).collect(Collectors.toList());
        }


        return listItemRequestDto;
    }
}
