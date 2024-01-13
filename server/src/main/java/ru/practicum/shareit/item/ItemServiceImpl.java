package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.error.baseExceptions.ForbiddenException;
import ru.practicum.shareit.error.baseExceptions.ValidationError;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.ItemNotFound;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.service.MapperService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.utils.Pagination;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final MapperService mapperService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final ItemRepository repository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        Item item = mapperService.toItem(itemDto);

        UserDto user = validateUser(userId);
        log.info("Создан новый предмет: " + item);
        item.setOwner(userMapper.toUser(user));

        return mapperService.toItemDto(repository.save(item), null);
    }

    @Override
    public ItemDto getItemById(Long id, Long userId) {
        Optional<Item> item = repository.findById(id);
        if (item.isEmpty()) {
            log.info(String.format("Предмет с id = %d не найден", id));
            throw new ItemNotFound("Вещь с данным id не найдена");
        }

        log.info("Получена информация о предмете с id = " + id);

        if (userId.equals(item.get().getOwner().getId())) {
            return mapperService.toItemDtoWithBooking(item.get(), getLastBooking(id), getNextBooking(id), getCommentsByItemId(id));
        } else {
            return mapperService.toItemDto(item.get(), getCommentsByItemId(id));
        }
    }

    @Override
    public ItemDto updateItem(Long id, Long userId, ItemDto itemDto) {
        Item item = mapperService.toItem(itemDto);

        if (repository.findById(id).isEmpty()) {
            log.info(String.format("Предмет с id = %d не найден", id));
            throw new ItemNotFound("Вещь с данным id не найдена");
        }

        Item foundItem = repository.findById(id).get();

        if (!userId.equals(foundItem.getOwner().getId())) {
            log.info("Попытка изменить вещь другим пользователем");
            throw new ForbiddenException("Текущий пользователь не может редактировать данную вещь");
        }

        UserDto user = validateUser(userId);

        item.setId(id);
        item.setOwner(userMapper.toUser(user));

        if (item.getName() == null) {
            item.setName(foundItem.getName());
        } else if (item.getName().isBlank()) {
            log.info("Обновление предмета не прошло валидацию названия (" + item + ")");
            throw new ValidationError("Название предмета не может быть пустым");
        }

        if (item.getDescription() == null) {
            item.setDescription(foundItem.getDescription());
        } else if (item.getDescription().isBlank()) {
            log.info("Обновление предмета не прошло валидацию описания (" + item + ")");
            throw new ValidationError("Описание предмета не может быть пустым");
        }

        if (item.getAvailable() == null) {
            item.setAvailable(foundItem.getAvailable());
        }

        if (item.getRequestId() == null) {
            item.setRequestId(foundItem.getRequestId());
        }

        log.info("Обновлён предмет: " + item);
        return mapperService.toItemDto(repository.save(item), getCommentsByItemId(id));
    }

    @Override
    public List<ItemDto> getAllItems(Long userId, Integer from, Integer size) {
        validateUser(userId);

        List<ItemDto> listItemDto = new ArrayList<>();
        Pageable pageable;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Page<Item> page;
        Pagination pager = new Pagination(from, size);

        if (size == null) {
            pageable = PageRequest.of(pager.getPageStart(), pager.getPageSize(), sort);
            page = repository.findByOwnerId(userId, pageable);

            while (page.hasContent()) {
                listItemDto.addAll(page.stream().map(
                        item -> mapperService.toItemDtoWithBooking(item, getLastBooking(item.getId()), getNextBooking(item.getId()), getCommentsByItemId(item.getId()))
                ).collect(Collectors.toList()));
                pageable = pageable.next();
                page = repository.findByOwnerId(userId, pageable);
            }
        } else {
            for (int i = pager.getPageStart(); i < pager.getPagesAmount(); i++) {
                pageable = PageRequest.of(i, pager.getPageSize(), sort);
                page = repository.findByOwnerId(userId, pageable);
                listItemDto.addAll(page.stream().map(
                        item -> mapperService.toItemDtoWithBooking(item, getLastBooking(item.getId()), getNextBooking(item.getId()), getCommentsByItemId(item.getId()))
                ).collect(Collectors.toList()));
            }

            listItemDto = listItemDto.stream().limit(size).collect(Collectors.toList());
        }

        log.info("Получен список всех предметов");
        return listItemDto;
    }

    @Override
    public List<ItemDto> searchItems(String text, Integer from, Integer size) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        List<ItemDto> listItemDto = new ArrayList<>();
        Pageable pageable;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Page<Item> page;
        Pagination pager = new Pagination(from, size);

        if (size == null) {
            pageable = PageRequest.of(pager.getPageStart(), pager.getPageSize(), sort);
            page = repository.search(text, pageable);

            while (page.hasContent()) {
                listItemDto.addAll(page.stream().map(
                        item -> mapperService.toItemDtoWithBooking(item, getLastBooking(item.getId()), getNextBooking(item.getId()), getCommentsByItemId(item.getId()))
                ).collect(Collectors.toList()));
                pageable = pageable.next();
                page = repository.search(text, pageable);
            }
        } else {
            for (int i = pager.getPageStart(); i < pager.getPagesAmount(); i++) {
                pageable = PageRequest.of(i, pager.getPageSize(), sort);
                page = repository.search(text, pageable);
                listItemDto.addAll(page.stream().map(
                        item -> mapperService.toItemDtoWithBooking(item, getLastBooking(item.getId()), getNextBooking(item.getId()), getCommentsByItemId(item.getId()))
                ).collect(Collectors.toList()));
            }

            listItemDto = listItemDto.stream().limit(size).collect(Collectors.toList());
        }

        log.info(String.format("Поиск предметов по подстроке \"%s\"", text));
        return listItemDto;
    }

    @Override
    public void deleteItem(Long id, Long userId) {
        if (repository.findById(id).isEmpty()) {
            log.info(String.format("Предмет с id = %d не найден", id));
            throw new ItemNotFound("Вещь с данным id не найдена");
        }

        Item foundItem = repository.findById(id).get();

        log.info("Удалён предмет с id = " + id);
        validateUser(userId);

        if (!userId.equals(foundItem.getOwner().getId())) {
            throw new ForbiddenException("Текущий пользователь не может удалить данную вещь");
        }

        repository.deleteById(id);
    }

    private UserDto validateUser(Long userId) {
        if (userId == null) {
            throw new ValidationError("Не указан id пользователя");
        }

        return userService.getUserById(userId);
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) {
        validateUser(userId);

        Comment comment = new Comment();
        Booking booking = bookingRepository.findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(itemId, userId, LocalDateTime.now(), BookingStatus.APPROVED);
        if (booking != null) {
            comment.setItem(booking.getItem());
            comment.setAuthor(booking.getBooker());
            comment.setText(commentDto.getText());
            comment.setCreated(LocalDateTime.now());
        } else {
            throw new ValidationError("Вещь не была забронирована данным пользователем");
        }

        return mapperService.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> getCommentsByItemId(Long itemId) {
        return commentRepository.findAllByItem_id(itemId, Sort.by(Sort.Direction.DESC, "created")).stream()
                .map(mapperService::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsByRequestId(Long requestId) {
        return repository.findAllByRequestId(requestId, Sort.by(Sort.Direction.DESC, "id")).stream()
                .map(item -> mapperService.toItemDto(item, getCommentsByItemId(item.getId())))
                .collect(Collectors.toList());
    }

    private BookingOutputDto getLastBooking(Long itemId) {
        Booking booking = bookingRepository.findFirstByItem_IdAndStartBeforeAndStatusOrderByStartDesc(itemId, LocalDateTime.now(), BookingStatus.APPROVED);
        if (booking != null) {
            return mapperService.toBookingOutputDto(booking);
        }

        return null;
    }

    private BookingOutputDto getNextBooking(Long itemId) {
        Booking booking = bookingRepository.findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(itemId, LocalDateTime.now(), BookingStatus.APPROVED);
        if (booking != null) {
            return mapperService.toBookingOutputDto(booking);
        }

        return null;
    }
}
