package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStartDate());
        dto.setEnd(booking.getEndDate());
        dto.setStatus(booking.getStatus());

        if (booking.getBooker() != null) {
            dto.setBooker(
                new UserDto(
                    booking.getBooker().getId(),
                    booking.getBooker().getName(),
                    booking.getBooker().getEmail()
                )
            );
        }


        if (booking.getItem() != null) {
            ItemDto itemDto = new ItemDto();
            itemDto.setId(booking.getItem().getId());
            itemDto.setName(booking.getItem().getName());
            dto.setItem(itemDto);
        }
        return dto;
    }

    public static Booking toBooking(BookingRequestDto bookingDto, User booker, Item item) {
        Booking booking = new Booking();
        booking.setStartDate(bookingDto.getStart());
        booking.setEndDate(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        return booking;
    }
}