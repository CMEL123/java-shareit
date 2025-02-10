package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    Long id;
    String name;
    String description;
    Boolean available;   //статус о том, доступна или нет вещь для аренды;
    User owner;          //владелец вещи;
    ItemRequest request; //если вещь была создана по запросу другого пользователя, то в этом
                         //   поле будет храниться ссылка на соответствующий запрос.
}