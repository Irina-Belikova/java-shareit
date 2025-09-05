package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;

import java.util.List;

public interface BookingService {

    BookingResponse addBooking(BookingRequest bookingRequest, long bookerId);

    BookingResponse updateBookingStatus(long bookingId, boolean approved);

    BookingResponse getBookingById(long bookingId);

    List<BookingResponse> getBookingsByBookerId(long bookerId, BookingState state);

    List<BookingResponse> getBookingsByOwnerItems(long ownerId, BookingState state);
}
