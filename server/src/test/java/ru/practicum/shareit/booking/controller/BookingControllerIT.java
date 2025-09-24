package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
class BookingControllerIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper mapper = new ObjectMapper();
    private static final String USER_ID = "X-Sharer-User-Id";

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setup() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        owner = userRepository.save(User.builder()
                .name("owner")
                .email("owner@mail.com")
                .build());

        booker = userRepository.save(User.builder()
                .name("booker")
                .email("booker@mail.com")
                .build());

        item = itemRepository.save(Item.builder()
                .name("item")
                .description("description")
                .available(true)
                .owner(owner)
                .build());

        booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build());
    }

    @Test
    void addBookingTest() throws Exception {
        BookingRequest bookingRequest = BookingRequest.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .itemId(item.getId())
                .build();

        mvc.perform(post("/bookings")
                        .header(USER_ID, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.item.id").value(item.getId()))
                .andExpect(jsonPath("$.booker.id").value(booker.getId()));
    }

    @Test
    void addBooking_whenItemAvailableIsFalse() throws Exception {
        Item falseItem = itemRepository.save(Item.builder()
                .name("item")
                .description("description")
                .available(false)
                .owner(owner)
                .build());

        BookingRequest bookingRequest = BookingRequest.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .itemId(falseItem.getId())
                .build();

        mvc.perform(post("/bookings")
                        .header(USER_ID, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBookingStatus_whenApprovedIsTrue() throws Exception {
        mvc.perform(patch("/bookings/{bookingId}", booking.getId())
                        .header(USER_ID, owner.getId())
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void updateBookingStatus_whenApprovedIsFalse() throws Exception {
        mvc.perform(patch("/bookings/{bookingId}", booking.getId())
                        .header(USER_ID, owner.getId())
                        .param("approved", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    void updateBookingStatus_whenNotOwner() throws Exception {
        mvc.perform(patch("/bookings/{bookingId}", booking.getId())
                        .header(USER_ID, booker.getId())
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingByIdTest() throws Exception {
        mvc.perform(get("/bookings/{bookingId}", booking.getId())
                        .header(USER_ID, owner.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.item.id").value(item.getId()))
                .andExpect(jsonPath("$.booker.id").value(booker.getId()));

        mvc.perform(get("/bookings/{bookingId}", booking.getId())
                        .header(USER_ID, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()));
    }

    @Test
    void getBookingById_whenOtherUser() throws Exception {
        User stranger = userRepository.save(User.builder()
                .name("stranger")
                .email("stranger@mail.com")
                .build());

        mvc.perform(get("/bookings/{bookingId}", booking.getId())
                        .header(USER_ID, stranger.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsByBookerId_shouldReturnBookings() throws Exception {
        bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build());

        mvc.perform(get("/bookings")
                        .header(USER_ID, booker.getId())
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].booker.id").value(booker.getId()))
                .andExpect(jsonPath("$[1].booker.id").value(booker.getId()));
    }

    @Test
    void getBookingsByBookerId_withDifferentStates() throws Exception {
        Booking currentBooking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build());

        mvc.perform(get("/bookings")
                        .header(USER_ID, booker.getId())
                        .param("state", "CURRENT")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(currentBooking.getId()));

        mvc.perform(get("/bookings")
                        .header(USER_ID, booker.getId())
                        .param("state", "FUTURE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(booking.getId()));
    }

    @Test
    void getBookingsByOwnerItemsTest() throws Exception {
        User otherOwner = userRepository.save(User.builder()
                .name("otherOwner")
                .email("otherOwner@mail.com")
                .build());

        Item otherItem = itemRepository.save(Item.builder()
                .name("otherItem")
                .description("otherDescription")
                .available(true)
                .owner(otherOwner)
                .build());

        bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(otherItem)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build());

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID, owner.getId())
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].booker").exists());

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID, otherOwner.getId())
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getBookingsByOwnerItems_whenUserHasNoItems() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .header(USER_ID, booker.getId())
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}