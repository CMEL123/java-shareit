package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    Long id;
    String name;
    @Email(message = "Электронная почта должна соответствовать шаблону name@domain.xx")
    String email;
}
