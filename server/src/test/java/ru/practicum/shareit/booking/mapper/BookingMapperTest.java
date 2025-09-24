package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class BookingMapperTest {

    @Autowired
    private BookingMapperImpl bookingMapper;

    @Test
    void mapToBookingTest() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingRequest bookingRequest = BookingRequest.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .bookerId(2L)
                .status(BookingStatus.WAITING)
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .build();

        User booker = User.builder()
                .id(2L)
                .name("booker")
                .email("booker@mail.com")
                .build();

        Booking booking = bookingMapper.mapToBooking(bookingRequest, item, booker);

        assertThat(booking).isNotNull();
        assertThat(booking.getId()).isNull();
        assertThat(booking.getStart()).isEqualTo(start);
        assertThat(booking.getEnd()).isEqualTo(end);
        assertThat(booking.getItem()).isEqualTo(item);
        assertThat(booking.getBooker()).isEqualTo(booker);
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void mapToBookingResponseTest() {
        User booker = User.builder()
                .id(2L)
                .name("booker")
                .email("booker@mail.com")
                .build();

        User owner = User.builder()
                .id(3L)
                .name("owner")
                .email("owner@mail.com")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        Booking booking = Booking.builder()
                .id(4L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        BookingResponse response = bookingMapper.mapToBookingResponse(booking);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(4L);
        assertThat(response.getStart()).isEqualTo(booking.getStart());
        assertThat(response.getEnd()).isEqualTo(booking.getEnd());
        assertThat(response.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(response.getItem()).isNotNull();
        assertThat(response.getBooker()).isNotNull();
    }

    @Test
    void mapToBookingResponseListTest() {
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();

        Booking booking2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .status(BookingStatus.APPROVED)
                .build();

        List<Booking> bookings = List.of(booking1, booking2);

        List<BookingResponse> responses = bookingMapper.mapToBookingResponseList(bookings);

        assertThat(responses.size()).isEqualTo(2);
        assertThat(responses.get(0).getId()).isEqualTo(1L);
        assertThat(responses.get(0).getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(responses.get(1).getId()).isEqualTo(2L);
        assertThat(responses.get(1).getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void mapToBookingResponseList_whenEmptyList() {
        List<BookingResponse> responses = bookingMapper.mapToBookingResponseList(List.of());

        assertThat(responses.size()).isEqualTo(0);
    }
}
