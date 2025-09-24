package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookingRepository bookingRepository;

    private User booker;
    private User owner;
    private Item item1;
    private Item item2;
    private Item item3;
    private Item item4;
    private Item item5;
    private Item item6;
    private Booking currentBooking;
    private Booking pastBooking;
    private Booking futureBooking;
    private Booking waitingBooking;
    private Booking approvedBooking;
    private Booking rejectedBooking;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        booker = User.builder()
                .name("Booker")
                .email("booker@example.com")
                .build();
        booker = em.persistAndFlush(booker);

        owner = User.builder()
                .name("Owner")
                .email("owner@example.com")
                .build();
        owner = em.persistAndFlush(owner);

        item1 = Item.builder()
                .name("item-1")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        item1 = em.persistAndFlush(item1);

        item2 = Item.builder()
                .name("item-2")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        item2 = em.persistAndFlush(item2);

        item3 = Item.builder()
                .name("item-3")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        item3 = em.persistAndFlush(item3);

        item4 = Item.builder()
                .name("item-4")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        item4 = em.persistAndFlush(item4);

        item5 = Item.builder()
                .name("item-5")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        item5 = em.persistAndFlush(item5);

        item6 = Item.builder()
                .name("item-6")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        item6 = em.persistAndFlush(item6);

        currentBooking = Booking.builder()
                .booker(booker)
                .item(item1)
                .start(now.minusDays(1))
                .end(now.plusDays(1))
                .status(BookingStatus.APPROVED)
                .build();
        currentBooking = em.persistAndFlush(currentBooking);

        pastBooking = Booking.builder()
                .booker(booker)
                .item(item2)
                .start(now.minusDays(5))
                .end(now.minusDays(2))
                .status(BookingStatus.APPROVED)
                .build();
        pastBooking = em.persistAndFlush(pastBooking);

        futureBooking = Booking.builder()
                .booker(booker)
                .item(item3)
                .start(now.plusDays(2))
                .end(now.plusDays(5))
                .status(BookingStatus.APPROVED)
                .build();
        futureBooking = em.persistAndFlush(futureBooking);

        waitingBooking = Booking.builder()
                .booker(booker)
                .item(item4)
                .start(now.plusDays(10))
                .end(now.plusDays(15))
                .status(BookingStatus.WAITING)
                .build();
        waitingBooking = em.persistAndFlush(waitingBooking);

        approvedBooking = Booking.builder()
                .booker(booker)
                .item(item5)
                .start(now.plusDays(20))
                .end(now.plusDays(25))
                .status(BookingStatus.APPROVED)
                .build();
        approvedBooking = em.persistAndFlush(approvedBooking);

        rejectedBooking = Booking.builder()
                .booker(booker)
                .item(item6)
                .start(now.plusDays(30))
                .end(now.plusDays(35))
                .status(BookingStatus.REJECTED)
                .build();
        rejectedBooking = em.persistAndFlush(rejectedBooking);
    }

    @Test
    void findByBookerId_returnedAllBookingsForBooker() {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        List<Booking> result = bookingRepository.findByBookerId(booker.getId(), sort);

        assertThat(result).hasSize(6);
        assertThat(result).extracting(Booking::getId)
                .containsExactly(
                        rejectedBooking.getId(),
                        approvedBooking.getId(),
                        waitingBooking.getId(),
                        futureBooking.getId(),
                        currentBooking.getId(),
                        pastBooking.getId()
                );
    }

    @Test
    void getAllCurrentByBookerId_returnedCurrentBookings() {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        List<Booking> result = bookingRepository.getAllCurrentByBookerId(booker.getId(), now, sort);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(currentBooking.getId());
    }

    @Test
    void findByBookerIdAndEndBefore_returnedPastBookings() {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        List<Booking> result = bookingRepository.findByBookerIdAndEndBefore(booker.getId(), now, sort);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(pastBooking.getId());
    }

    @Test
    void findByBookerIdAndStartAfter_returnedFutureBookings() {
        Sort sort = Sort.by(Sort.Direction.ASC, "start");

        List<Booking> result = bookingRepository.findByBookerIdAndStartAfter(booker.getId(), now, sort);

        assertThat(result).hasSize(4);
        assertThat(result).extracting(Booking::getId)
                .containsExactly(
                        futureBooking.getId(),
                        waitingBooking.getId(),
                        approvedBooking.getId(),
                        rejectedBooking.getId()
                );
    }

    @Test
    void findByBookerIdAndStatus_returnedBookingsByStatus() {
        Sort sort = Sort.by(Sort.Direction.ASC, "start");

        List<Booking> waitingResult = bookingRepository.findByBookerIdAndStatus(booker.getId(), BookingStatus.WAITING, sort);
        List<Booking> approvedResult = bookingRepository.findByBookerIdAndStatus(booker.getId(), BookingStatus.APPROVED, sort);
        List<Booking> rejectedResult = bookingRepository.findByBookerIdAndStatus(booker.getId(), BookingStatus.REJECTED, sort);

        assertThat(waitingResult).hasSize(1);
        assertThat(waitingResult.get(0).getId()).isEqualTo(waitingBooking.getId());

        assertThat(approvedResult).hasSize(4);
        assertThat(approvedResult).extracting(Booking::getId)
                .containsExactly(pastBooking.getId(), currentBooking.getId(), futureBooking.getId(), approvedBooking.getId());

        assertThat(rejectedResult).hasSize(1);
        assertThat(rejectedResult.get(0).getId()).isEqualTo(rejectedBooking.getId());
    }

    @Test
    void getAllByOwnerId_returnedAllBookingsForOwner() {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        List<Booking> result = bookingRepository.getAllByOwnerId(owner.getId(), sort);

        assertThat(result).hasSize(6);
        assertThat(result).extracting(Booking::getId)
                .containsExactly(
                        rejectedBooking.getId(),
                        approvedBooking.getId(),
                        waitingBooking.getId(),
                        futureBooking.getId(),
                        currentBooking.getId(),
                        pastBooking.getId()
                );
    }

    @Test
    void getAllCurrentByOwnerId_returnedCurrentBookingsForOwner() {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        List<Booking> result = bookingRepository.getAllCurrentByOwnerId(owner.getId(), now, sort);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(currentBooking.getId());
    }

    @Test
    void getAllPastByOwnerId_returnedPastBookingsForOwner() {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        List<Booking> result = bookingRepository.getAllPastByOwnerId(owner.getId(), now, sort);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(pastBooking.getId());
    }

    @Test
    void getAllFutureByOwnerId_returnedFutureBookingsForOwner() {
        Sort sort = Sort.by(Sort.Direction.ASC, "start");

        List<Booking> result = bookingRepository.getAllFutureByOwnerId(owner.getId(), now, sort);

        assertThat(result).hasSize(4);
        assertThat(result).extracting(Booking::getId)
                .containsExactly(
                        futureBooking.getId(),
                        waitingBooking.getId(),
                        approvedBooking.getId(),
                        rejectedBooking.getId()
                );
    }

    @Test
    void getAllByOwnerIdAndStatus_returnedBookingsByStatusForOwner() {
        Sort sort = Sort.by(Sort.Direction.ASC, "start");

        List<Booking> waitingResult = bookingRepository.getAllByOwnerIdAndStatus(owner.getId(), BookingStatus.WAITING, sort);
        List<Booking> approvedResult = bookingRepository.getAllByOwnerIdAndStatus(owner.getId(), BookingStatus.APPROVED, sort);
        List<Booking> rejectedResult = bookingRepository.getAllByOwnerIdAndStatus(owner.getId(), BookingStatus.REJECTED, sort);

        assertThat(waitingResult).hasSize(1);
        assertThat(waitingResult.get(0).getId()).isEqualTo(waitingBooking.getId());

        assertThat(approvedResult).hasSize(4);
        assertThat(approvedResult).extracting(Booking::getId)
                .containsExactly(pastBooking.getId(), currentBooking.getId(), futureBooking.getId(), approvedBooking.getId());

        assertThat(rejectedResult).hasSize(1);
        assertThat(rejectedResult.get(0).getId()).isEqualTo(rejectedBooking.getId());
    }

    @Test
    void findByBookerIdAndItemId_returnedBookingWhenExists() {
        Optional<Booking> result = bookingRepository.findByBookerIdAndItemId(booker.getId(), item1.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getBooker().getId()).isEqualTo(booker.getId());
        assertThat(result.get().getItem().getId()).isEqualTo(item1.getId());
    }

    @Test
    void findByBookerIdAndItemId_returnedEmptyWhenNotExists() {
        Optional<Booking> result = bookingRepository.findByBookerIdAndItemId(999L, 999L);

        assertThat(result).isEmpty();
    }

    @Test
    void getLastBookingByItemId_returnedCurrentBookingStartTime() {
        LocalDateTime result = bookingRepository.getLastBookingByItemId(item1.getId(), now);

        assertThat(result).isEqualToIgnoringNanos(currentBooking.getStart());
    }

    @Test
    void getNextBookingByItemId_returnedNextFutureBookingStartTime() {
        LocalDateTime result = bookingRepository.getNextBookingByItemId(item3.getId(), now);

        assertThat(result).isAfter(now);
    }

    @Test
    void getAllLastBookingsByItemIds_returnedMapOfLastBookings() {
        List<Long> itemIds = List.of(item1.getId());

        Map<Long, LocalDateTime> result = bookingRepository.getAllLastBookingsByItemIds(itemIds, now);

        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void getAllNextBookingsByItemIds_returnedMapOfNextBookings() {
        List<Long> itemIds = List.of(item3.getId());

        Map<Long, LocalDateTime> result = bookingRepository.getAllNextBookingsByItemIds(itemIds, now);

        assertThat(result).hasSize(1);
        assertThat(result.get(item3.getId())).isEqualToIgnoringNanos(futureBooking.getStart());
    }

    @Test
    void getAllLastBookingsByItemIds_returnedEmptyList() {
        List<Long> emptyItemIds = List.of();

        Map<Long, LocalDateTime> result = bookingRepository.getAllLastBookingsByItemIds(emptyItemIds, now);

        assertThat(result).isEmpty();
    }

    @Test
    void getAllNextBookingsByItemIds_returnedEmptyList() {
        List<Long> emptyItemIds = List.of();

        Map<Long, LocalDateTime> result = bookingRepository.getAllNextBookingsByItemIds(emptyItemIds, now);

        assertThat(result).isEmpty();
    }

    @Test
    void getAllLastBookingsByItemIds_whenNonExistingItems() {
        List<Long> nonExistingItemIds = List.of(999L);

        Map<Long, LocalDateTime> result = bookingRepository.getAllLastBookingsByItemIds(nonExistingItemIds, now);

        assertThat(result).isEmpty();
    }

    @Test
    void findByBookerId_returnedEmptyListForNonExistingBooker() {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        List<Booking> result = bookingRepository.findByBookerId(999L, sort);

        assertThat(result).isEmpty();
    }

    @Test
    void getAllByOwnerId_returnedEmptyListForNonExistingOwner() {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        List<Booking> result = bookingRepository.getAllByOwnerId(999L, sort);

        assertThat(result).isEmpty();
    }
}
