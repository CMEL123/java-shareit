package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    Long id;
    String name;
    String description;
    Boolean available;   //статус о том, доступна или нет вещь для аренды;
    Long owner;          //владелец вещи;
    Long request; //если вещь была создана по запросу другого пользователя, то в этом
                         //   поле будет храниться ссылка на соответствующий запрос.

}
