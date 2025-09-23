package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    private static final Sort SORT_START = Sort.by(Sort.Direction.DESC, "start");

    @Override
    @Transactional
    public BookingResponse addBooking(BookingRequest bookingRequest, long bookerId) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с таким id - %s не найден.", bookerId)));
        Item item = itemRepository.findById(bookingRequest.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с таким id - %s не найдена.", bookingRequest.getItemId())));
        Booking booking = bookingMapper.mapToBooking(bookingRequest, item, booker);
        booking.setStatus(BookingStatus.WAITING);
        booking = bookingRepository.save(booking);
        return bookingMapper.mapToBookingResponse(booking);
    }

    @Override
    @Transactional
    public BookingResponse updateBookingStatus(long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирование с таким id - %s не найдено.", bookingId)));

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingMapper.mapToBookingResponse(booking);
    }

    @Override
    public BookingResponse getBookingById(long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирование с таким id - %s не найдено.", bookingId)));
        return bookingMapper.mapToBookingResponse(booking);
    }

    @Override
    public List<BookingResponse> getBookingsByBookerId(long bookerId, BookingState state) {
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL -> bookings = bookingRepository.findByBookerId(bookerId, SORT_START);
            case CURRENT ->
                    bookings = bookingRepository.getAllCurrentByBookerId(bookerId, LocalDateTime.now(), SORT_START);
            case PAST ->
                    bookings = bookingRepository.findByBookerIdAndEndBefore(bookerId, LocalDateTime.now(), SORT_START);
            case FUTURE ->
                    bookings = bookingRepository.findByBookerIdAndStartAfter(bookerId, LocalDateTime.now(), SORT_START);
            case WAITING ->
                    bookings = bookingRepository.findByBookerIdAndStatus(bookerId, BookingStatus.WAITING, SORT_START);
            case REJECTED ->
                    bookings = bookingRepository.findByBookerIdAndStatus(bookerId, BookingStatus.REJECTED, SORT_START);
        }
        return bookingMapper.mapToBookingResponseList(bookings);
    }

    @Override
    public List<BookingResponse> getBookingsByOwnerItems(long ownerId, BookingState state) {
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL -> bookings = bookingRepository.getAllByOwnerId(ownerId, SORT_START);
            case CURRENT ->
                    bookings = bookingRepository.getAllCurrentByOwnerId(ownerId, LocalDateTime.now(), SORT_START);
            case PAST -> bookings = bookingRepository.getAllPastByOwnerId(ownerId, LocalDateTime.now(), SORT_START);
            case FUTURE -> bookings = bookingRepository.getAllFutureByOwnerId(ownerId, LocalDateTime.now(), SORT_START);
            case WAITING ->
                    bookings = bookingRepository.getAllByOwnerIdAndStatus(ownerId, BookingStatus.WAITING, SORT_START);
            case REJECTED ->
                    bookings = bookingRepository.getAllByOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, SORT_START);
        }
        return bookingMapper.mapToBookingResponseList(bookings);
    }
}
