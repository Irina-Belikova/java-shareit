package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.ValidationUtils;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemService itemService;

    @Mock
    private ValidationUtils validation;

    private ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private static final long VALID_USER_ID = 1L;
    private static final long VALID_ITEM_ID = 1L;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController)
                .setControllerAdvice(new ErrorHandler())
                .build();
    }

    @Test
    void addItem_whenValidData() throws Exception {
        ItemDto itemDto = ItemDto.builder().build();
        ItemDto responseDto = ItemDto.builder().id(VALID_ITEM_ID).build();

        doNothing().when(validation).checkUserId(VALID_USER_ID);
        when(itemService.addItem(any(ItemDto.class), eq(VALID_USER_ID))).thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VALID_ITEM_ID));
    }

    @Test
    void addItem_whenMissingUserIdHeader() throws Exception {
        ItemDto itemDto = ItemDto.builder().build();

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem_whenValidData() throws Exception {
        ItemDto itemDto = ItemDto.builder().build();
        ItemDto responseDto = ItemDto.builder().id(VALID_ITEM_ID).build();

        doNothing().when(validation).validationForUpdateItem(VALID_USER_ID, VALID_ITEM_ID);
        when(itemService.updateItem(eq(VALID_ITEM_ID), any(ItemDto.class))).thenReturn(responseDto);

        mockMvc.perform(patch("/items/{itemId}", VALID_ITEM_ID)
                        .header(USER_ID_HEADER, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VALID_ITEM_ID));
    }

    @Test
    void updateItem_whenValidationFails() throws Exception {
        ItemDto itemDto = ItemDto.builder().build();

        doThrow(new ValidationException("Validation failed"))
                .when(validation).validationForUpdateItem(VALID_USER_ID, 9L);

        mockMvc.perform(patch("/items/{itemId}", 9L)
                        .header(USER_ID_HEADER, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemById_whenValidData() throws Exception {
        ItemDto responseDto = ItemDto.builder().id(VALID_ITEM_ID).build();

        doNothing().when(validation).checkUserId(VALID_USER_ID);
        doNothing().when(validation).checkItemId(VALID_ITEM_ID);
        when(itemService.getItemById(VALID_ITEM_ID)).thenReturn(responseDto);

        mockMvc.perform(get("/items/{itemId}", VALID_ITEM_ID)
                        .header(USER_ID_HEADER, VALID_USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VALID_ITEM_ID));
    }

    @Test
    void getItemById_whenItemNotFound() throws Exception {
        doNothing().when(validation).checkUserId(VALID_USER_ID);
        doThrow(new ValidationException("Item not found"))
                .when(validation).checkItemId(9L);

        mockMvc.perform(get("/items/{itemId}", 9L)
                        .header(USER_ID_HEADER, VALID_USER_ID))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemsByUserId_whenValidData() throws Exception {
        List<ItemDto> items = List.of(
                ItemDto.builder().id(1L).build(),
                ItemDto.builder().id(2L).build()
        );

        doNothing().when(validation).checkUserId(VALID_USER_ID);
        when(itemService.getItemsByUserId(VALID_USER_ID)).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header(USER_ID_HEADER, VALID_USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void getItemsByText_whenValidText() throws Exception {
        List<ItemDto> items = List.of(ItemDto.builder().id(1L).name("Drill").build());

        doNothing().when(validation).checkUserId(VALID_USER_ID);
        when(itemService.getItemsByText("drill")).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .header(USER_ID_HEADER, VALID_USER_ID)
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Drill"));
    }

    @Test
    void getItemsByText_whenBlankText() throws Exception {
        mockMvc.perform(get("/items/search")
                        .header(USER_ID_HEADER, VALID_USER_ID)
                        .param("text", "   "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void addComment_whenValidData() throws Exception {
        CommentRequest commentRequest = CommentRequest.builder().text("comment").build();
        CommentResponse commentResponse = CommentResponse.builder().id(1L).text("comment").build();

        when(itemService.addComment(eq(VALID_USER_ID), eq(VALID_ITEM_ID), any(CommentRequest.class)))
                .thenReturn(commentResponse);

        mockMvc.perform(post("/items/{itemId}/comment", VALID_ITEM_ID)
                        .header(USER_ID_HEADER, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("comment"));
    }

    @Test
    void addComment_whenInvalidItemId() throws Exception {
        CommentRequest commentRequest = CommentRequest.builder().text("comment").build();

        doThrow(new ValidationException("bad id"))
                .when(validation).validationForCreateComment(VALID_USER_ID, 9L);

        mockMvc.perform(post("/items/{itemId}/comment", 9L)
                        .header(USER_ID_HEADER, VALID_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentRequest)))
                .andExpect(status().isBadRequest());
    }
}
