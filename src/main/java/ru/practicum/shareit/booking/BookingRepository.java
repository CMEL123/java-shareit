package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDateDesc(Long bookerId);

    List<Booking> findByItemOwnerIdOrderByStartDateDesc(Long ownerId);

    List<Booking> findByItemIdAndEndDateBeforeOrderByEndDateDesc(Long itemId, LocalDateTime now);

    List<Booking> findByItemIdAndEndDateAfterOrderByEndDate(Long itemId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(Long bookerId,
                                                                                      LocalDateTime startDate,
                                                                                      LocalDateTime EndDate);

    List<Booking> findByBookerIdAndEndDateBeforeOrderByEndDateDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartDateAfterOrderByStartDateDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStatusOrderByStartDateDesc(Long bookerId, Status status);

    List<Booking> findByItemOwnerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(Long ownerId,
                                                                                         LocalDateTime startDate,
                                                                                         LocalDateTime endDate);

    List<Booking> findByItemOwnerIdAndEndDateBeforeOrderByStartDateDesc(Long ownerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStartDateAfterOrderByStartDateDesc(Long ownerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDateDesc(Long ownerId, Status status);

    List<Booking> findByBookerIdAndItemIdAndStatusAndEndDateBefore(Long ownerId,
                                                                   Long itemId,
                                                                   Status status,
                                                                   LocalDateTime now);
}