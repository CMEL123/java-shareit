package ru.practicum.shareit.booking;

public enum BookingState {
    ALL,     // Все
    CURRENT, // Текущие
    PAST,    // Завершённые
    FUTURE,  // Будущие
    WAITING, // Ожидающие подтверждения
    REJECTED // Отклонённые
}
