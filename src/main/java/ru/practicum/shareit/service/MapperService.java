package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MapperService {
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    public UserDto toUserDto(User user) {
        return userMapper.toUserDto(user);
    }

    public User toUser(UserDto userDto) {
        return userMapper.toUser(userDto);
    }

    public ItemDto toItemDto(Item item, List<CommentDto> comments) {
        return itemMapper.toItemDto(item, toUserDto(item.getOwner()), comments);
    }

    public Item toItem(ItemDto item) {
        return itemMapper.toItem(item, toUser(item.getOwner()));
    }

    public ItemDto toItemDtoWithBooking(Item item, BookingOutputDto lastBooking, BookingOutputDto nextBooking, List<CommentDto> comments) {
        return itemMapper.toItemDtoWithBooking(item, toUserDto(item.getOwner()), lastBooking, nextBooking, comments);
    }

    public BookingDto toBookingDto(Booking booking) {
        return bookingMapper.toBookingDto(booking, toItemDto(booking.getItem(), null), toUserDto(booking.getBooker()));
    }

    public Booking toBooking(BookingDto bookingDto) {
        return bookingMapper.toBooking(bookingDto, toItem(bookingDto.getItem()), toUser(bookingDto.getBooker()));
    }

    public Booking toBooking(BookingInputDto bookingInputDto, ItemDto itemDto, UserDto userDto) {
        return bookingMapper.toBooking(bookingInputDto, toItem(itemDto), toUser(userDto));
    }

    public BookingOutputDto toBookingOutputDto(Booking booking) {
        return bookingMapper.toBookingOutputDto(booking);
    }

    public CommentDto toCommentDto(Comment comment) {
        return commentMapper.toCommentDto(comment);
    }
}
