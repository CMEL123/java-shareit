package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    Long id;
    String name;
    String description;
    Boolean available;   //статус о том, доступна или нет вещь для аренды;
    Long ownerId;          //владелец вещи;
    Long requestId; //если вещь была создана по запросу другого пользователя, то в этом
                         //   поле будет храниться ссылка на соответствующий запрос.
    List<BookingDto> bookings;

}
