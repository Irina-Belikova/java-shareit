package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingClient bookingClient;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private static final long VALID_USER_ID = 1L;
    private static final long VALID_BOOKING_ID = 1L;

    @Test
    void addBooking_whenValidData() throws Exception {
        BookingRequest bookingRequest = BookingRequest.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .bookerId(VALID_USER_ID)
                .build();

        BookingResponse bookingResponse = BookingResponse.builder()
                .id(VALID_BOOKING_ID)
                .build();

        when(bookingClient.addBooking(eq(VALID_USER_ID), any(BookingRequest.class)))
                .thenReturn(ResponseEntity.ok(bookingResponse));

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VALID_BOOKING_ID));
    }

    @Test
    void addBooking_whenInvalidUserId() throws Exception {
        BookingRequest bookingRequest = BookingRequest.builder().build();

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, -1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBooking_whenEndBeforeStart() throws Exception {
        BookingRequest bookingRequest = BookingRequest.builder()
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .itemId(1L)
                .bookerId(VALID_USER_ID)
                .build();

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBooking_whenMissingUserIdHeader() throws Exception {
        BookingRequest bookingRequest = BookingRequest.builder().build();

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBookingStatus_whenValidData() throws Exception {
        BookingResponse bookingResponse = BookingResponse.builder()
                .id(VALID_BOOKING_ID)
                .build();

        when(bookingClient.updateBookingStatus(eq(VALID_USER_ID), eq(VALID_BOOKING_ID), eq(true)))
                .thenReturn(ResponseEntity.ok(bookingResponse));

        mockMvc.perform(patch("/bookings/{bookingId}", VALID_BOOKING_ID)
                        .header(USER_ID_HEADER, VALID_USER_ID)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VALID_BOOKING_ID));
    }

    @Test
    void updateBookingStatus_whenBadBookingId() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", -1L)
                        .header(USER_ID_HEADER, VALID_USER_ID)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBookingStatus_whenMissingApprovedParam() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", VALID_BOOKING_ID)
                        .header(USER_ID_HEADER, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingById_whenValidRequest() throws Exception {
        BookingResponse bookingResponse = BookingResponse.builder()
                .id(VALID_BOOKING_ID)
                .build();

        when(bookingClient.getBookingById(eq(VALID_USER_ID), eq(VALID_BOOKING_ID)))
                .thenReturn(ResponseEntity.ok(bookingResponse));

        mockMvc.perform(get("/bookings/{bookingId}", VALID_BOOKING_ID)
                        .header(USER_ID_HEADER, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VALID_BOOKING_ID));
    }

    @Test
    void getBookingById_whenBadBookingId() throws Exception {
        mockMvc.perform(get("/bookings/{bookingId}", -1L)
                        .header(USER_ID_HEADER, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsByBookerId_whenValidData() throws Exception {
        when(bookingClient.getBookingsByBookerId(eq(VALID_USER_ID), any(BookingState.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, VALID_USER_ID)
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingsByBookerId_whenDifferentStates() throws Exception {
        BookingState[] states = BookingState.values();

        for (BookingState state : states) {
            when(bookingClient.getBookingsByBookerId(eq(VALID_USER_ID), eq(state)))
                    .thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(get("/bookings")
                            .header(USER_ID_HEADER, VALID_USER_ID)
                            .param("state", state.name())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void getBookingsByBookerId_whenBadUserId() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, -1L)
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsByOwnerItems_whenValidData() throws Exception {
        when(bookingClient.getBookingsByOwnerItems(eq(VALID_USER_ID), any(BookingState.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, VALID_USER_ID)
                        .param("state", "CURRENT")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingsByOwnerItems_whenDifferentStates() throws Exception {
        BookingState[] states = BookingState.values();

        for (BookingState state : states) {
            when(bookingClient.getBookingsByOwnerItems(eq(VALID_USER_ID), eq(state)))
                    .thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(get("/bookings/owner")
                            .header(USER_ID_HEADER, VALID_USER_ID)
                            .param("state", state.name())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void getBookingsByOwnerItems_whenBadUserId() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, -1L)
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsByOwnerItems_whenDefaultState() throws Exception {
        when(bookingClient.getBookingsByOwnerItems(eq(VALID_USER_ID), eq(BookingState.ALL)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
