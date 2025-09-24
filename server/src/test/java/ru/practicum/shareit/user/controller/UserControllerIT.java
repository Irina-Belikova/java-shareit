package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
class UserControllerIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    private UserDto userDto;
    private User user1;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        userDto = UserDto.builder()
                .name("name")
                .email("name@mail.com")
                .build();

        user1 = User.builder()
                .name("Kot")
                .email("kot@mail.com")
                .build();
    }

    @Test
    void createUser_whenValidData() throws Exception {
        MvcResult result = mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        JsonNode jsonNode = mapper.readTree(response);
        Long id = jsonNode.get("id").asLong();

        Optional<User> savedUser = userRepository.findById(id);
        assertThat(savedUser).isPresent();
    }

    @Test
    void createUser_whenDuplicateEmail_thenThrownDuplicateDataException() throws Exception {
         mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isConflict());

        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    void updateUser_whenValidData_andFailData() throws Exception {
        User savedUser = userRepository.save(user1);
        long id = savedUser.getId();

        MvcResult result = mvc.perform(patch("/users/{userId}", id)
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        JsonNode jsonNode = mapper.readTree(response);
        Long userId = jsonNode.get("id").asLong();

        Optional<User> updateUser = userRepository.findById(userId);

        assertEquals("name", updateUser.get().getName());
        assertEquals("name@mail.com", updateUser.get().getEmail());

        mvc.perform(patch("/users/{userId}", id + 1)
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        User newUser = userRepository.save(new User(null, "newName", "newPochta@mail.com"));
        mvc.perform(patch("/users/{userId}", newUser.getId())
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void getUserById_withValidAndNotData() throws Exception {
        User savedUser = userRepository.save(user1);
        long id = savedUser.getId();

        mvc.perform(get("/users/{userId}", id)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(savedUser.getName())))
                .andExpect(jsonPath("$.email", is(savedUser.getEmail())));

        mvc.perform(get("/users/{userId}", id + 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());;
    }

    @Test
    void deleteUserById() throws Exception {
        User savedUser = userRepository.save(user1);
        long id = savedUser.getId();

        mvc.perform(delete("/users/{userId}", id)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(userRepository.count()).isEqualTo(0);

        mvc.perform(delete("/users/{userId}", id)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }
}