package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public BookingDto createBooking(BookingRequestDto bookingDto, Long bookerId) {
        log.info("Запрос бронирования {}", bookingDto);
        User booker = checkUser(bookerId);
        Item item = checkItem(bookingDto.getItemId());
        Booking booking = BookingMapper.toBooking(bookingDto, booker, item);
        cheakNewBooking(booking);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Transactional
    public BookingDto approveBooking(Long bookingId, Boolean approved, Long ownerId) {
        log.info("Установка статуса {} бронирования id = {}", approved, bookingId);
        Booking booking = checkBooking(bookingId);

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new AccessException(String.format("Пользователь с id = %d не является владельцем вещи с id = %d",
                    ownerId, booking.getItem().getOwner().getId()));
        }

        checkUser(ownerId);
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    public BookingDto findById(Long bookingId, Long userId) {
        log.info("Найти бронирование по id = {}, id пользователь = {}, который ищет", bookingId, userId);
        User bookerOrOwner = checkUser(userId);
        Booking booking = checkBooking(bookingId);
        if (!booking.getItem().getOwner().equals(bookerOrOwner) && !booking.getBooker().equals(bookerOrOwner)) {
            throw new ValidationException("Пользователь не является владельцем вещи или автором бронирования");
        }
        return BookingMapper.toBookingDto(booking);
    }

    public List<BookingDto> findAll(BookingState state, Long bookerId) {
        log.info("Получение списка всех бронирований = {} текущего пользователя = {}", state, bookerId);
        checkState(state);
        checkUser(bookerId);
        List<Booking> bookings;
        if (state == BookingState.ALL) {
            bookings = bookingRepository.findByBookerIdOrderByStartDateDesc(bookerId);
        } else {
            LocalDateTime now = LocalDateTime.now();
            bookings = switch (state) {
                case CURRENT -> bookingRepository.findByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(bookerId, now,
                        now);
                case PAST -> bookingRepository.findByBookerIdAndEndDateBeforeOrderByEndDateDesc(bookerId, now);
                case FUTURE -> bookingRepository.findByBookerIdAndStartDateAfterOrderByStartDateDesc(bookerId, now);
                case WAITING -> bookingRepository.findByBookerIdAndStatusOrderByStartDateDesc(bookerId, Status.WAITING);
                case REJECTED -> bookingRepository.findByBookerIdAndStatusOrderByStartDateDesc(bookerId, Status.REJECTED);
                default -> throw new ValidationException(String.format("Некорректное состояние бронирования: %s",
                        state));
            };
        }
        return bookings.stream().map(BookingMapper::toBookingDto).toList();
    }

    public List<BookingDto> findAllOwner(BookingState state, Long ownerId) {
        log.info("Получение списка бронирований  = {} для всех вещей текущего пользователя = {}", state, ownerId);
        checkState(state);
        checkUser(ownerId);
        List<Booking> bookings;
        if (state == BookingState.ALL) {
            bookings = bookingRepository.findByItemOwnerIdOrderByStartDateDesc(ownerId);
        } else {
            LocalDateTime now = LocalDateTime.now();
            bookings = switch (state) {
                case CURRENT -> bookingRepository.findByItemOwnerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
                        ownerId,
                        now,
                        now);
                case PAST -> bookingRepository.findByItemOwnerIdAndEndDateBeforeOrderByStartDateDesc(ownerId, now);
                case FUTURE -> bookingRepository.findByItemOwnerIdAndStartDateAfterOrderByStartDateDesc(ownerId, now);
                case WAITING -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartDateDesc(ownerId, Status.WAITING);
                case REJECTED -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartDateDesc(ownerId, Status.REJECTED);
                default -> throw new ValidationException(String.format("Некорректное состояние бронирования: %s",
                        state));
            };
        }
        log.info("Получено {} бронирований ({}) для владельца с id = {}", bookings.size(), state, ownerId);
        return bookings.stream().map(BookingMapper::toBookingDto).toList();
    }

    private Booking checkBooking(Long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isPresent()) {
            log.info("Бронирование c id = {} найдено", bookingId);
            return booking.get();
        }
        log.warn("Бронирование с id = {} не найдено", bookingId);
        throw new NotFoundException(String.format("Бронирование с id=%d не найдено", bookingId));
    }

    private void checkState(BookingState state) {
        if (state == null) {
            throw new ValidationException(String.format("Статус бронирования не указан, state = %s", state));
        }
        if (!EnumSet.allOf(BookingState.class).contains(state)) {
            throw new ValidationException(String.format("Передан некорректный статус бронирования: %s", state));
        }
    }

    private User checkUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if  (user.isPresent()) {
            log.info("Пользователь c id = {} найден", userId);
            return user.get();
        }
        log.warn("Пользователь с id = {} не найден", userId);
        throw new NotFoundException(String.format("Пользователь с id=%d не найден", userId));
    }

    private Item checkItem(Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if  (item.isPresent()) {
            log.info("Вещь c id = {} найдена", itemId);
            return item.get();
        }
        log.warn("Вещь с id = {} не найдена", itemId);
        throw new NotFoundException(String.format("Вещь с id=%d не найдена", itemId));
    }

    private void cheakNewBooking(Booking booking) {
        if (booking.getStartDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Начала бронирования не может быть меньше текущего времени");
        }
        if (booking.getEndDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Конец бронирования не может быть меньше текущего времени");
        }
        if (booking.getEndDate().isBefore(booking.getStartDate())) {
            throw new ValidationException("Начала бронирования не может быть позже окончания бронирования");
        }
        if (booking.getEndDate().equals(booking.getStartDate())) {
            throw new ValidationException("Начала бронирования не может быть равным окончанию бронирования");
        }
        if (!booking.getItem().getAvailable()) {
            throw new ValidationException("Вещь не доступна");
        }
    }
}