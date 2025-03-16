package ru.practicum.shareit.booking.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDto {
    @NotBlank(message = "Время начала должно быть указано")
    LocalDateTime start;

    @NotBlank(message = "Время окончания должно быть указано")
    LocalDateTime end;

    Long itemId;

    Long bookerId;
}