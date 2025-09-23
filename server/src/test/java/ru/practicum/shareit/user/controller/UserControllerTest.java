package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.DuplicateDataException;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.ValidationUtils;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private ValidationUtils validation;

    private final ObjectMapper mapper = new ObjectMapper();

    private UserDto userDto;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new ErrorHandler())
                .build();
        userDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("name@mail.com")
                .build();
    }

    @Test
    void createUser_whenInvokedIsValid() throws Exception {
        Mockito.when(userService.createUser(any())).thenReturn(userDto);
        Mockito.doNothing().when(validation).validationForCreateUser(any());

        mvc.perform(post("/users")
                .content(mapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        InOrder inOrder = inOrder(validation, userService);
        inOrder.verify(validation).validationForCreateUser(any());
        inOrder.verify(userService).createUser(any());
    }

    @Test
    void createUserWithException() throws Exception {
        Mockito.doThrow(new DuplicateDataException("Такой email - %s уже существует."))
                .when(validation).validationForCreateUser(any());

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                        .andExpect(jsonPath("$.message").exists());

        InOrder inOrder = inOrder(validation, userService);
        inOrder.verify(validation).validationForCreateUser(any());
        inOrder.verify(userService, never()).createUser(any());
    }

    @Test
    void updateUser_whenValidData_thenReturnUpdateUser() throws Exception {
        long userId = 1L;
        UserDto updateUser = UserDto.builder()
                .name("newName")
                .email("newMail@mail.com")
                .build();
        Mockito.when(userService.updateUser(userId, updateUser)).thenReturn(updateUser);
        Mockito.doNothing().when(validation).validationForUpdateUser(userId, updateUser);

        mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(updateUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updateUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updateUser.getName())))
                .andExpect(jsonPath("$.email", is(updateUser.getEmail())));

        InOrder inOrder = inOrder(validation, userService);
        inOrder.verify(validation).validationForUpdateUser(userId, updateUser);
        inOrder.verify(userService).updateUser(userId, updateUser);
    }

    @Test
    void updateUser_whenDuplicateEmail_thenThrowDuplicateDataException() throws Exception {
        long userId = 1L;
        Mockito.doThrow(new DuplicateDataException("Такой email - %s уже существует."))
                        .when(validation).validationForUpdateUser(eq(userId), any());

        mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());

        InOrder inOrder = inOrder(validation, userService);
        inOrder.verify(validation).validationForUpdateUser(eq(userId), any());
        inOrder.verify(userService, never()).updateUser(anyLong(), any());
    }

    @Test
    void updateUser_whenUserNotFound_thenThrowNotFoundException() throws Exception {
        long userId = 1L;
        Mockito.doThrow(new NotFoundException("Пользователя с таким id не существует."))
                .when(validation).validationForUpdateUser(eq(userId), any());

        mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());

        InOrder inOrder = inOrder(validation, userService);
        inOrder.verify(validation).validationForUpdateUser(eq(userId), any());
        inOrder.verify(userService, never()).updateUser(anyLong(), any());
    }

    @Test
    void getUserById_whenValidData() throws Exception {
        long userId = 1L;
        Mockito.when(userService.getUserById(userId)).thenReturn(userDto);
        Mockito.doNothing().when(validation).checkUserId(userId);

        mvc.perform(get("/users/{userId}", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        InOrder inOrder = inOrder(validation, userService);
        inOrder.verify(validation).checkUserId(userId);
        inOrder.verify(userService).getUserById(userId);
    }

    @Test
    void getUserById_whenUserNotFound_thenThrowNotFoundException() throws Exception {
        long userId = 1L;
        Mockito.doThrow(new NotFoundException("Пользователя с таким id не существует."))
                .when(validation).checkUserId(userId);

        mvc.perform(get("/users/{userId}", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());

        InOrder inOrder = inOrder(validation, userService);
        inOrder.verify(validation).checkUserId(userId);
        inOrder.verify(userService, never()).getUserById(userId);
    }

    @Test
    void deleteUserById_whenValidData() throws Exception {
        long userId = 1L;
        Mockito.doNothing().when(userService).deleteUserById(userId);
        Mockito.doNothing().when(validation).checkUserId(userId);

        mvc.perform(delete("/users/{userId}", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        InOrder inOrder = inOrder(validation, userService);
        inOrder.verify(validation).checkUserId(userId);
        inOrder.verify(userService).deleteUserById(userId);
    }

    @Test
    void deleteUserById_whenUserNotFound_thenThrowNotFoundException() throws Exception {
        long userId = 1L;
        Mockito.doThrow(new NotFoundException("Пользователя с таким id не существует."))
                .when(validation).checkUserId(userId);

        mvc.perform(delete("/users/{userId}", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());

        InOrder inOrder = inOrder(validation, userService);
        inOrder.verify(validation).checkUserId(userId);
        inOrder.verify(userService, never()).deleteUserById(userId);
    }
}