package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.mapper.UserMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static Booking mapToBooking(BookingRequest bookingRequest, Item item, User booker) {
        return Booking.builder()
                .start(bookingRequest.getStart())
                .end(bookingRequest.getEnd())
                .item(item)
                .booker(booker)
                .status(bookingRequest.getStatus())
                .build();
    }

    public static BookingResponse mapToBookingResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.mapToItemDto(booking.getItem()))
                .booker(UserMapper.mapToUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }
}
