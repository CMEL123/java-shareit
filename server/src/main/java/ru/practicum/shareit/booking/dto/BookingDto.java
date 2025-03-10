package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDto {
    Long id;
    @NotBlank(message = "Время начала должно быть указано")
    LocalDateTime start;
    @NotBlank(message = "Время окончания должно быть указано")
    LocalDateTime end;
    ItemDto item;
    UserDto booker;
    Status status;
}