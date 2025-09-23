package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.validation.ValidationUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private BookingController bookingController;

    @Mock
    private BookingService bookingService;

    @Mock
    private ValidationUtils validation;

    private ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private static final long VALID_USER_ID = 1L;
    private static final long VALID_BOOKING_ID = 1L;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController)
                .setControllerAdvice(new ErrorHandler())
                .build();
    }

    @Test
    void addBooking_whenValidData() throws Exception {
        BookingRequest bookingRequest = BookingRequest.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .bookerId(VALID_USER_ID)
                .build();

        BookingResponse bookingResponse = BookingResponse.builder().id(VALID_BOOKING_ID).build();

        doNothing().when(validation).validationForCreateBooking(any(BookingRequest.class), eq(VALID_USER_ID));
        when(bookingService.addBooking(any(BookingRequest.class), eq(VALID_USER_ID))).thenReturn(bookingResponse);

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VALID_BOOKING_ID));
    }

    @Test
    void updateBookingStatus_whenValidData() throws Exception {
        BookingResponse bookingResponse = BookingResponse.builder()
                .id(VALID_BOOKING_ID)
                .status(BookingStatus.APPROVED)
                .build();

        doNothing().when(validation).validationForUpdateBookingStatus(VALID_BOOKING_ID, VALID_USER_ID);
        when(bookingService.updateBookingStatus(VALID_BOOKING_ID, true)).thenReturn(bookingResponse);

        mockMvc.perform(patch("/bookings/{bookingId}", VALID_BOOKING_ID)
                        .header(USER_ID_HEADER, VALID_USER_ID)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VALID_BOOKING_ID))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBookingById_whenValidData() throws Exception {
        BookingResponse bookingResponse = BookingResponse.builder().id(VALID_BOOKING_ID).build();

        doNothing().when(validation).validationForGetBookingById(VALID_BOOKING_ID, VALID_USER_ID);
        when(bookingService.getBookingById(VALID_BOOKING_ID)).thenReturn(bookingResponse);

        mockMvc.perform(get("/bookings/{bookingId}", VALID_BOOKING_ID)
                        .header(USER_ID_HEADER, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VALID_BOOKING_ID));
    }

    @Test
    void getBookingsByBookerId_whenValidData() throws Exception {
        List<BookingResponse> bookings = List.of(
                BookingResponse.builder().id(1L).build(),
                BookingResponse.builder().id(2L).build()
        );

        doNothing().when(validation).checkUserId(VALID_USER_ID);
        when(bookingService.getBookingsByBookerId(VALID_USER_ID, BookingState.ALL)).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, VALID_USER_ID)
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void getBookingsByOwnerItems_whenValidData() throws Exception {
        List<BookingResponse> bookings = List.of(
                BookingResponse.builder().id(1L).build(),
                BookingResponse.builder().id(2L).build()
        );

        doNothing().when(validation).validationOwnerHasItems(VALID_USER_ID);
        when(bookingService.getBookingsByOwnerItems(VALID_USER_ID, BookingState.ALL)).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, VALID_USER_ID)
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void getBookingsByBookerId_whenDefaultState() throws Exception {
        List<BookingResponse> bookings = List.of(BookingResponse.builder().id(1L).build());

        doNothing().when(validation).checkUserId(VALID_USER_ID);
        when(bookingService.getBookingsByBookerId(VALID_USER_ID, BookingState.ALL)).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getBookingsByOwnerItems_whenDefaultState() throws Exception {
        List<BookingResponse> bookings = List.of(BookingResponse.builder().id(1L).build());

        doNothing().when(validation).validationOwnerHasItems(VALID_USER_ID);
        when(bookingService.getBookingsByOwnerItems(VALID_USER_ID, BookingState.ALL)).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}