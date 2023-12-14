package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ItemMapper {
    public ItemDto toItemDto(Item item, UserDto userDto, List<CommentDto> comments) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                userDto,
                item.getRequest(),
                null,
                null,
                comments
        );
    }

    public Item toItem(ItemDto itemDto, User user) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user,
                itemDto.getRequest()
        );
    }

    public ItemDto toItemDtoWithBooking(Item item, UserDto userDto, BookingOutputDto lastBooking, BookingOutputDto nextBooking, List<CommentDto> comments) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                userDto,
                item.getRequest(),
                lastBooking,
                nextBooking,
                comments
        );
    }
}
