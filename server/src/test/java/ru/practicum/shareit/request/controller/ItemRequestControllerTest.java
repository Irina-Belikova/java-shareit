package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.validation.ValidationUtils;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private ItemRequestController itemRequestController;

    @Mock
    private ItemRequestService requestService;

    @Mock
    private ValidationUtils validation;

    private ObjectMapper mapper = new ObjectMapper();
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private static final long VALID_USER_ID = 1L;
    private static final long VALID_REQUEST_ID = 1L;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemRequestController)
                .setControllerAdvice(new ErrorHandler())
                .build();
    }

    @Test
    void addRequest_whenValidData() throws Exception {
        ItemRequestRequest request = ItemRequestRequest.builder()
                .description("нужна дрель")
                .build();

        ItemRequestResponse response = ItemRequestResponse.builder()
                .id(VALID_REQUEST_ID)
                .description("нужна дрель")
                .build();

        doNothing().when(validation).checkUserId(VALID_USER_ID);
        when(requestService.addRequest(any(ItemRequestRequest.class), eq(VALID_USER_ID))).thenReturn(response);

        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VALID_REQUEST_ID))
                .andExpect(jsonPath("$.description").value("нужна дрель"));
    }

    @Test
    void addRequest_whenMissingUserIdHeader() throws Exception {
        ItemRequestRequest request = ItemRequestRequest.builder().build();

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRequestsByRequestor_whenValidData() throws Exception {
        List<ItemRequestResponse> responses = List.of(
                ItemRequestResponse.builder().id(1L).description("request-1").build(),
                ItemRequestResponse.builder().id(2L).description("request-2").build()
        );

        doNothing().when(validation).checkUserId(VALID_USER_ID);
        when(requestService.getRequestsByRequestor(VALID_USER_ID)).thenReturn(responses);

        mockMvc.perform(get("/requests")
                        .header(USER_ID_HEADER, VALID_USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("request-1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].description").value("request-2"));
    }

    @Test
    void getRequestsByRequestor_whenUserNotFound() throws Exception {
        doThrow(new ValidationException("пользователь не найден"))
                .when(validation).checkUserId(9L);

        mockMvc.perform(get("/requests")
                        .header(USER_ID_HEADER, 9L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRequestsByOtherRequestor_whenValidData() throws Exception {
        List<ItemRequestResponse> responses = List.of(
                ItemRequestResponse.builder().id(1L).build(),
                ItemRequestResponse.builder().id(2L).build()
        );

        doNothing().when(validation).checkUserId(VALID_USER_ID);
        when(requestService.getRequestsByOtherRequestor(VALID_USER_ID)).thenReturn(responses);

        mockMvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, VALID_USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void getRequestsByOtherRequestor_whenInvalidUserId() throws Exception {
        doThrow(new ValidationException("Invalid user ID")).when(validation).checkUserId(-1L);

        mockMvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, -1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRequestById_whenValidData() throws Exception {
        ItemRequestResponse response = ItemRequestResponse.builder()
                .id(VALID_REQUEST_ID)
                .description("request")
                .build();

        doNothing().when(validation).checkUserId(VALID_USER_ID);
        doNothing().when(validation).checkRequestId(VALID_REQUEST_ID);
        when(requestService.getRequestById(VALID_REQUEST_ID)).thenReturn(response);

        mockMvc.perform(get("/requests/{requestId}", VALID_REQUEST_ID)
                        .header(USER_ID_HEADER, VALID_USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VALID_REQUEST_ID))
                .andExpect(jsonPath("$.description").value("request"));
    }

    @Test
    void getRequestById_whenRequestNotFound() throws Exception {
        doNothing().when(validation).checkUserId(VALID_USER_ID);
        doThrow(new ValidationException("запрос не найден"))
                .when(validation).checkRequestId(9L);

        mockMvc.perform(get("/requests/{requestId}", 9L)
                        .header(USER_ID_HEADER, VALID_USER_ID))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRequestById_whenMissingUserIdHeader() throws Exception {
        mockMvc.perform(get("/requests/{requestId}", VALID_REQUEST_ID))
                .andExpect(status().isBadRequest());
    }
}
