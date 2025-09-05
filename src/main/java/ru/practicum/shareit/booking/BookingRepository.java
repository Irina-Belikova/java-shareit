package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerId(long bookerId, Sort sort);

    @Query("SELECT b FROM Booking b " +
           "WHERE b.booker.id = ?1 " +
           "AND ?2 BETWEEN b.start AND b.end")
    List<Booking> getAllCurrentByBookerId(long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndEndBefore(long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndStartAfter(long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndStatus(long bookerId, BookingStatus status, Sort sort);

    @Query("SELECT b FROM Booking b " +
           "WHERE b.item.owner.id = ?1")
    List<Booking> getAllByOwnerId(long ownerId, Sort sort);

    @Query("SELECT b FROM Booking b " +
           "WHERE b.item.owner.id = ?1 " +
           "AND ?2 BETWEEN b.start AND b.end")
    List<Booking> getAllCurrentByOwnerId(long ownerId, LocalDateTime now, Sort sort);

    @Query("SELECT b FROM Booking b " +
           "WHERE b.item.owner.id = ?1 " +
           "AND end < ?2")
    List<Booking> getAllPastByOwnerId(long ownerId, LocalDateTime now, Sort sort);

    @Query("SELECT b FROM Booking b " +
           "WHERE b.item.owner.id = ?1 " +
           "AND start > ?2")
    List<Booking> getAllFutureByOwnerId(long ownerId, LocalDateTime now, Sort sort);

    @Query("SELECT b FROM Booking b " +
           "WHERE b.item.owner.id = ?1 " +
           "AND status = ?2")
    List<Booking> getAllByOwnerIdAndStatus(long ownerId, BookingStatus status, Sort sort);

    Optional<Booking> findByBookerIdAndItemId(long bookerId, long itemId);

    @Query("SELECT b.start FROM Booking b " +
           "WHERE b.item.id = ?1 " +
           "AND ?2 BETWEEN b.start AND b.end")
    LocalDateTime getLastBookingByItemId(long itemId, LocalDateTime now);

    @Query("SELECT MIN(b.start) FROM Booking b " +
           "WHERE b.item.id = ?1 " +
           "AND b.start > ?2")
    LocalDateTime getNextBookingByItemId(long itemId, LocalDateTime now);

    @Query("SELECT b.item.id, b.start FROM Booking b " +
           "WHERE b.item.id IN ?1 " +
           "AND ?2 BETWEEN b.start AND b.end")
    List<Object[]> getAllLastBookings(List<Long> itemIds, LocalDateTime now);

    default Map<Long, LocalDateTime> getAllLastBookingsByItemIds(List<Long> itemIds, LocalDateTime now) {
        List<Object[]> objects = getAllLastBookings(itemIds, now);
        return objects.stream()
                .collect(Collectors.toMap(
                        object -> (Long) object[0],
                        object -> (LocalDateTime) object[1]
                ));
    }

    @Query("SELECT b.item.id, MIN(b.start) FROM Booking b " +
           "WHERE b.item.id IN ?1 " +
           "AND b.start > ?2 " +
           "GROUP BY b.item.id")
    List<Object[]> getAllNextBookings(List<Long> itemIds, LocalDateTime now);

    default Map<Long, LocalDateTime> getAllNextBookingsByItemIds(List<Long> itemIds, LocalDateTime now) {
        List<Object[]> objects = getAllNextBookings(itemIds, now);
        return objects.stream()
                .collect(Collectors.toMap(
                        object -> (Long) object[0],
                        object -> (LocalDateTime) object[1]
                ));
    }
}
