package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsCommentsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.comment.CommentMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
            item.getId(),
            item.getName(),
            item.getDescription(),
            item.getAvailable(),
            item.getOwner() != null ? item.getOwner().getId() : null,
            item.getRequest() != null ? item.getRequest().getId() : null,
            new ArrayList<>()
        );
    }

    public static ItemDto toItemDto(Item item, List<Booking> bookings, List<Comment> comments) {
        return  new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner() != null ? item.getOwner().getId() : null,
                item.getRequest() != null ? item.getRequest().getId() : null,
                bookings.stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList())
        );
    }

    public static Item toItem(ItemResponseDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

    public static ItemWithBookingsCommentsDto toItemWithBookingsCommentsDto(
            Item item, Optional<Booking> lastBooking, Optional<Booking> nextBooking,List<Comment> comments) {
        ItemWithBookingsCommentsDto dto = new ItemWithBookingsCommentsDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwner().getId());
        lastBooking.ifPresent(booking -> dto.setLastBooking(BookingMapper.toBookingDto(booking)));
        nextBooking.ifPresent(booking -> dto.setNextBooking(BookingMapper.toBookingDto(booking)));
        dto.setComments(comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
        return dto;
    }

    public static ItemResponseDto toItemResponseDto(Item item) {
        ItemResponseDto dto = new ItemResponseDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setOwnerId(item.getOwner() != null ? item.getOwner().getId() : null);
        return dto;
    }
}
