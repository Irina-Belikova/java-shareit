package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
class ItemRequestControllerIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ItemRequestRepository repository;

    @Autowired
    private EntityManager em;

    private final ObjectMapper mapper = new ObjectMapper();

    private User requestor;
    private User owner;
    private ItemRequest request1;
    private Item item;
    private static final String USER_ID = "X-Sharer-User-Id";

    @BeforeEach
    void setup() {
        em.clear();
        repository.deleteAll();

        requestor = createUser("requestor", "pochta@mail.com");
        request1 = createRequest("description", requestor, LocalDateTime.now().minusDays(1));
        owner = createUser("ownerItem", "email@mail.com");
    }


    @Test
    void addRequest_whenValidData() throws Exception {
        ItemRequestRequest request = ItemRequestRequest.builder().description("request for some item").build();

        MvcResult result = mvc.perform(post("/requests")
                        .header(USER_ID, requestor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        JsonNode jsonNode = mapper.readTree(response);
        Long id = jsonNode.get("id").asLong();

        Optional<ItemRequest> savedRequest = repository.findById(id);
        assertThat(savedRequest).isPresent();
    }

    @Test
    void addRequest_whenFailUserId() throws Exception {
        long userId = 9L;
        ItemRequestRequest request = ItemRequestRequest.builder().description("request for some item").build();

        mvc.perform(post("/requests")
                        .header(USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());

        assertThat(repository.count()).isEqualTo(0);
    }

    @Test
    void getRequestsByRequestor_whenUserExists() throws Exception {
        ItemRequest savedRequest = repository.save(request1);
        item = createItem("item", owner, savedRequest);
        long requestorId = requestor.getId();

        mvc.perform(get("/requests")
                        .header(USER_ID, requestorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(savedRequest.getId()))
                .andExpect(jsonPath("$[0].description").value(savedRequest.getDescription()))
                .andExpect(jsonPath("$[0].items").isArray())
                .andExpect(jsonPath("$[0].items.length()").value(1));
    }

    @Test
    void getRequestsByRequestor_whenUserNotFound() throws Exception {
        long requestorId = 9L;

        mvc.perform(get("/requests")
                        .header(USER_ID, requestorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value(String.format("Пользователя с таким id - %s не существует.", requestorId)));
    }

    @Test
    void getRequestsByOtherRequestor_whenUserExists() throws Exception {
        ItemRequest savedRequest = repository.save(request1);
        item = createItem("item", owner, savedRequest);
        long requestorId = requestor.getId();

        User otherRequestor = createUser("name", "name@mail.com");
        ItemRequest request2 = createRequest("description2", otherRequestor, LocalDateTime.now());
        ItemRequest savedRequest2 = repository.save(request2);

        mvc.perform(get("/requests/all")
                        .header(USER_ID, requestorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(savedRequest2.getId()))
                .andExpect(jsonPath("$[0].description").value(savedRequest2.getDescription()))
                .andExpect(jsonPath("$[0].items").isArray())
                .andExpect(jsonPath("$[0].items.length()").value(0));
    }

    @Test
    void getRequestsByOtherRequestor_whenUserNotFound() throws Exception {
        long requestorId = 9L;

        mvc.perform(get("/requests/all")
                        .header(USER_ID, requestorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value(String.format("Пользователя с таким id - %s не существует.", requestorId)));

    }

    @Test
    void getRequestById_whenValidData() throws Exception {
        ItemRequest savedRequest = repository.save(request1);
        item = createItem("item", owner, savedRequest);
        long requestorId = requestor.getId();
        long id = savedRequest.getId();

        mvc.perform(get("/requests/{requestId}", id)
                        .header(USER_ID, requestorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedRequest.getId()))
                .andExpect(jsonPath("$.description").value(savedRequest.getDescription()))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(1));

    }

    @Test
    void getRequestById_whenUserNotFound() throws Exception {
        ItemRequest savedRequest = repository.save(request1);
        long id = savedRequest.getId();
        long requestorId = 9L;

        mvc.perform(get("/requests/{requestId}", id)
                        .header(USER_ID, requestorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getRequestById_whenRequestIdNotFound() throws Exception {
        long id = 1L;
        long requestorId = requestor.getId();

        mvc.perform(get("/requests/{requestId}", id)
                        .header(USER_ID, requestorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }


    private User createUser(String name, String email) {
        User user = User.builder().name(name).email(email).build();
        em.persist(user);
        return user;
    }

    private ItemRequest createRequest(String description, User requestor, LocalDateTime created) {
        return ItemRequest.builder()
                .description(description)
                .requestor(requestor)
                .created(created)
                .build();
    }

    private Item createItem(String name, User owner, ItemRequest request) {
        Item item = Item.builder()
                .name(name)
                .description("Description")
                .available(true)
                .owner(owner)
                .request(request)
                .build();
        em.persist(item);
        return item;
    }
}