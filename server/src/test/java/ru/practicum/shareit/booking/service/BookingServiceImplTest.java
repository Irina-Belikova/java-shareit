package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {


    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private static final Sort SORT_START = Sort.by(Sort.Direction.DESC, "start");

    @Test
    void addBooking_whenValidData() {
        long bookerId = 1L;
        long itemId = 2L;
        BookingRequest bookingRequest = BookingRequest.builder().itemId(itemId).build();
        User booker = User.builder().id(bookerId).build();
        Item item = Item.builder().id(itemId).build();
        Booking booking = Booking.builder().build();
        Booking savedBooking = Booking.builder().build();
        BookingResponse response = BookingResponse.builder().build();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingMapper.mapToBooking(bookingRequest, item, booker)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(savedBooking);
        when(bookingMapper.mapToBookingResponse(savedBooking)).thenReturn(response);

        BookingResponse result = bookingService.addBooking(bookingRequest, bookerId);

        assertThat(result).isEqualTo(response);
        verify(bookingRepository).save(booking);
    }

    @Test
    void addBooking_whenUserNotFound() {
        long bookerId = 9L;
        BookingRequest bookingRequest = BookingRequest.builder().itemId(1L).build();

        when(userRepository.findById(bookerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(bookingRequest, bookerId));

        verify(itemRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void addBooking_whenItemNotFound() {
        long bookerId = 1L;
        long itemId = 9L;
        BookingRequest bookingRequest = BookingRequest.builder().itemId(itemId).build();
        User booker = User.builder().id(bookerId).build();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(bookingRequest, bookerId));

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void updateBookingStatusTest() {
        long bookingId = 1L;
        Booking booking = Booking.builder().id(bookingId).status(BookingStatus.WAITING).build();
        BookingResponse response = BookingResponse.builder().id(bookingId).status(BookingStatus.APPROVED).build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingMapper.mapToBookingResponse(booking)).thenReturn(response);

        BookingResponse result = bookingService.updateBookingStatus(bookingId, true);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.APPROVED);
        verify(bookingRepository).findById(bookingId);
    }

    @Test
    void updateBookingStatus_whenApprovedFalse() {
        long bookingId = 1L;
        Booking booking = Booking.builder().id(bookingId).status(BookingStatus.WAITING).build();
        BookingResponse response = BookingResponse.builder().id(bookingId).status(BookingStatus.REJECTED).build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingMapper.mapToBookingResponse(booking)).thenReturn(response);

        BookingResponse result = bookingService.updateBookingStatus(bookingId, false);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.REJECTED);
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void updateBookingStatus_whenBookingNotFound() {
        long bookingId = 999L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.updateBookingStatus(bookingId, true));
    }

    @Test
    void getBookingByIdTest() {
        long bookingId = 1L;
        Booking booking = Booking.builder().id(bookingId).build();
        BookingResponse response = BookingResponse.builder().id(bookingId).build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingMapper.mapToBookingResponse(booking)).thenReturn(response);

        BookingResponse result = bookingService.getBookingById(bookingId);

        assertThat(result).isEqualTo(response);
        verify(bookingRepository).findById(bookingId);
    }

    @Test
    void getBookingById_whenBookingNotFound() {
        long bookingId = 9L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(bookingId));
    }

    @Test
    void getBookingsByBookerIdTest() {
        long bookerId = 1L;
        Booking booking1 = Booking.builder().id(1L).build();
        Booking booking2 = Booking.builder().id(2L).build();
        List<Booking> bookings = List.of(booking1, booking2);
        BookingResponse response1 = BookingResponse.builder().id(1L).build();
        BookingResponse response2 = BookingResponse.builder().id(2L).build();
        List<BookingResponse> expected = List.of(response1, response2);

        when(bookingRepository.findByBookerId(bookerId, SORT_START)).thenReturn(bookings);
        when(bookingMapper.mapToBookingResponseList(bookings)).thenReturn(expected);

        List<BookingResponse> result = bookingService.getBookingsByBookerId(bookerId, BookingState.ALL);

        assertThat(result.size()).isEqualTo(2);
        assertThat(result).isEqualTo(expected);
        verify(bookingRepository).findByBookerId(bookerId, SORT_START);
    }

    @Test
    void getBookingsByBookerId_returnedCurrentBookings() {
        long bookerId = 1L;
        List<Booking> bookings = List.of(Booking.builder().id(1L).build());
        List<BookingResponse> expected = List.of(BookingResponse.builder().id(1L).build());

        when(bookingRepository.getAllCurrentByBookerId(eq(bookerId), any(LocalDateTime.class), eq(SORT_START)))
                .thenReturn(bookings);
        when(bookingMapper.mapToBookingResponseList(bookings)).thenReturn(expected);

        List<BookingResponse> result = bookingService.getBookingsByBookerId(bookerId, BookingState.CURRENT);

        assertThat(result.size()).isEqualTo(1);
        verify(bookingRepository).getAllCurrentByBookerId(eq(bookerId), any(LocalDateTime.class), eq(SORT_START));
    }

    @Test
    void getBookingsByBookerId_returnedPastBookings() {
        long bookerId = 1L;
        List<Booking> bookings = List.of(Booking.builder().id(1L).build());

        when(bookingRepository.findByBookerIdAndEndBefore(eq(bookerId), any(LocalDateTime.class), eq(SORT_START)))
                .thenReturn(bookings);
        when(bookingMapper.mapToBookingResponseList(bookings)).thenReturn(List.of());

        List<BookingResponse> result = bookingService.getBookingsByBookerId(bookerId, BookingState.PAST);

        assertThat(result.size()).isEqualTo(0);
        verify(bookingRepository).findByBookerIdAndEndBefore(eq(bookerId), any(LocalDateTime.class), eq(SORT_START));
    }

    @Test
    void getBookingsByBookerId_returnedFutureBookings() {
        long bookerId = 1L;
        List<Booking> bookings = List.of(Booking.builder().id(1L).build());

        when(bookingRepository.findByBookerIdAndStartAfter(eq(bookerId), any(LocalDateTime.class), eq(SORT_START)))
                .thenReturn(bookings);
        when(bookingMapper.mapToBookingResponseList(bookings)).thenReturn(List.of());

        List<BookingResponse> result = bookingService.getBookingsByBookerId(bookerId, BookingState.FUTURE);

        assertThat(result.size()).isEqualTo(0);
        verify(bookingRepository).findByBookerIdAndStartAfter(eq(bookerId), any(LocalDateTime.class), eq(SORT_START));
    }

    @Test
    void getBookingsByBookerId_returnedWaitingBookings() {
        long bookerId = 1L;
        List<Booking> bookings = List.of(Booking.builder().id(1L).build());

        when(bookingRepository.findByBookerIdAndStatus(bookerId, BookingStatus.WAITING, SORT_START))
                .thenReturn(bookings);
        when(bookingMapper.mapToBookingResponseList(bookings)).thenReturn(List.of());

        List<BookingResponse> result = bookingService.getBookingsByBookerId(bookerId, BookingState.WAITING);

        assertThat(result.size()).isEqualTo(0);
        verify(bookingRepository).findByBookerIdAndStatus(bookerId, BookingStatus.WAITING, SORT_START);
    }

    @Test
    void getBookingsByBookerId_returnedRejectedBookings() {
        long bookerId = 1L;
        List<Booking> bookings = List.of(Booking.builder().id(1L).build());

        when(bookingRepository.findByBookerIdAndStatus(bookerId, BookingStatus.REJECTED, SORT_START))
                .thenReturn(bookings);
        when(bookingMapper.mapToBookingResponseList(bookings)).thenReturn(List.of());

        List<BookingResponse> result = bookingService.getBookingsByBookerId(bookerId, BookingState.REJECTED);

        assertThat(result.size()).isEqualTo(0);
        verify(bookingRepository).findByBookerIdAndStatus(bookerId, BookingStatus.REJECTED, SORT_START);
    }

    @Test
    void getBookingsByOwnerItems_returnedAllBookings() {
        long ownerId = 1L;
        List<Booking> bookings = List.of(Booking.builder().id(1L).build());
        List<BookingResponse> expected = List.of(BookingResponse.builder().id(1L).build());

        when(bookingRepository.getAllByOwnerId(ownerId, SORT_START)).thenReturn(bookings);
        when(bookingMapper.mapToBookingResponseList(bookings)).thenReturn(expected);

        List<BookingResponse> result = bookingService.getBookingsByOwnerItems(ownerId, BookingState.ALL);

        assertThat(result.size()).isEqualTo(1);
        verify(bookingRepository).getAllByOwnerId(ownerId, SORT_START);
    }

    @Test
    void getBookingsByOwnerItems_returnedCurrentBookings() {
        long ownerId = 1L;
        List<Booking> bookings = List.of(Booking.builder().id(1L).build());

        when(bookingRepository.getAllCurrentByOwnerId(eq(ownerId), any(LocalDateTime.class), eq(SORT_START)))
                .thenReturn(bookings);
        when(bookingMapper.mapToBookingResponseList(bookings)).thenReturn(List.of());

        List<BookingResponse> result = bookingService.getBookingsByOwnerItems(ownerId, BookingState.CURRENT);

        assertThat(result.size()).isEqualTo(0);
        verify(bookingRepository).getAllCurrentByOwnerId(eq(ownerId), any(LocalDateTime.class), eq(SORT_START));
    }

    @Test
    void getBookingsByOwnerItems_returnedPastBookings() {
        long ownerId = 1L;
        List<Booking> bookings = List.of(Booking.builder().id(1L).build());

        when(bookingRepository.getAllPastByOwnerId(eq(ownerId), any(LocalDateTime.class), eq(SORT_START)))
                .thenReturn(bookings);
        when(bookingMapper.mapToBookingResponseList(bookings)).thenReturn(List.of());

        List<BookingResponse> result = bookingService.getBookingsByOwnerItems(ownerId, BookingState.PAST);

        assertThat(result.size()).isEqualTo(0);
        verify(bookingRepository).getAllPastByOwnerId(eq(ownerId), any(LocalDateTime.class), eq(SORT_START));
    }

    @Test
    void getBookingsByOwnerItems_returnedFutureBookings() {
        long ownerId = 1L;
        List<Booking> bookings = List.of(Booking.builder().id(1L).build());

        when(bookingRepository.getAllFutureByOwnerId(eq(ownerId), any(LocalDateTime.class), eq(SORT_START)))
                .thenReturn(bookings);
        when(bookingMapper.mapToBookingResponseList(bookings)).thenReturn(List.of());

        List<BookingResponse> result = bookingService.getBookingsByOwnerItems(ownerId, BookingState.FUTURE);

        assertThat(result.size()).isEqualTo(0);
        verify(bookingRepository).getAllFutureByOwnerId(eq(ownerId), any(LocalDateTime.class), eq(SORT_START));
    }

    @Test
    void getBookingsByOwnerItems_returnedWaitingBookings() {
        long ownerId = 1L;
        List<Booking> bookings = List.of(Booking.builder().id(1L).build());

        when(bookingRepository.getAllByOwnerIdAndStatus(ownerId, BookingStatus.WAITING, SORT_START))
                .thenReturn(bookings);
        when(bookingMapper.mapToBookingResponseList(bookings)).thenReturn(List.of());

        List<BookingResponse> result = bookingService.getBookingsByOwnerItems(ownerId, BookingState.WAITING);

        assertThat(result.size()).isEqualTo(0);
        verify(bookingRepository).getAllByOwnerIdAndStatus(ownerId, BookingStatus.WAITING, SORT_START);
    }

    @Test
    void getBookingsByOwnerItems_returnedRejectedBookings() {
        long ownerId = 1L;
        List<Booking> bookings = List.of(Booking.builder().id(1L).build());

        when(bookingRepository.getAllByOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, SORT_START))
                .thenReturn(bookings);
        when(bookingMapper.mapToBookingResponseList(bookings)).thenReturn(List.of());

        List<BookingResponse> result = bookingService.getBookingsByOwnerItems(ownerId, BookingState.REJECTED);

        assertThat(result.size()).isEqualTo(0);
        verify(bookingRepository).getAllByOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, SORT_START);
    }

    @Test
    void getBookingsByBookerId_returnedEmptyListWhenNoBookings() {
        long bookerId = 1L;

        when(bookingRepository.findByBookerId(bookerId, SORT_START)).thenReturn(List.of());
        when(bookingMapper.mapToBookingResponseList(List.of())).thenReturn(List.of());

        List<BookingResponse> result = bookingService.getBookingsByBookerId(bookerId, BookingState.ALL);

        assertThat(result.size()).isEqualTo(0);
    }
}
