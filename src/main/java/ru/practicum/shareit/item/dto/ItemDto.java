package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    private Boolean available;
    private UserDto owner;
    private Long requestId;
    private BookingOutputDto lastBooking;
    private BookingOutputDto nextBooking;
    private List<CommentDto> comments;
}
