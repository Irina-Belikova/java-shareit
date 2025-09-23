package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
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
class ItemControllerIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private EntityManager em;

    private final ObjectMapper mapper = new ObjectMapper();

    private static final String USER_ID = "X-Sharer-User-Id";

    private User author;
    private User owner;
    private ItemDto dto1;
    private ItemDto dto2;
    private Item item1;
    private Item item2;

    @BeforeEach
    void setup() {
        em.clear();
        itemRepository.deleteAll();

        author = createUser("author", "author@mail.com");
        owner = createUser("owner", "owner@mail.com");

        dto1 = createItemDto("item-1", "description-1", null, true);
        dto2 = createItemDto("item-2", "description-2", null, true);

        item1 = createItem("item-1", "description-1", owner, true);
        item2 = createItem("item-2", "description-2", owner, false);
    }

    @Test
    void addItem_whenValidData() throws Exception {
        MvcResult result = mvc.perform(post("/items")
                        .header(USER_ID, owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto1)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        JsonNode jsonNode = mapper.readTree(response);
        Long id = jsonNode.get("id").asLong();

        Optional<Item> savedItem = itemRepository.findById(id);
        assertThat(savedItem).isPresent();
    }

    @Test
    void addItem_whenOwnerNotFound() throws Exception {
        long id = 9L;

        mvc.perform(post("/items")
                        .header(USER_ID, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto1)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());

        assertThat(itemRepository.count()).isEqualTo(0);
    }

    @Test
    void updateItemTest() throws Exception {
        Item savedItem = itemRepository.save(item1);
        long itemId = savedItem.getId();

        mvc.perform(patch("/items/{itemId}", itemId)
                        .header(USER_ID, owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(dto2.getName()))
                .andExpect(jsonPath("$.description").value(dto2.getDescription()));
    }

    @Test
    void updateItem_whenUserNotOwner() throws Exception {
        Item savedItem = itemRepository.save(item1);
        long itemId = savedItem.getId();

        mvc.perform(patch("/items/{itemId}", itemId)
                        .header(USER_ID, author.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getItemById_whenExists() throws Exception {
        Item savedItem = itemRepository.save(item1);
        long itemId = savedItem.getId();

        mvc.perform(get("/items/{itemId}", itemId)
                        .header(USER_ID, owner.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value(savedItem.getName()))
                .andExpect(jsonPath("$.description").value(savedItem.getDescription()))
                .andExpect(jsonPath("$.available").value(savedItem.getAvailable()))
                .andExpect(jsonPath("$.lastBooking").doesNotExist())
                .andExpect(jsonPath("$.nextBooking").doesNotExist())
                .andExpect(jsonPath("$.comments").isArray());
    }

    @Test
    void getItemById_whenItemNotFound_shouldReturnNotFound() throws Exception {
        mvc.perform(get("/items/{itemId}", 9L)
                        .header(USER_ID, owner.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getItemById_whenUserNotFound() throws Exception {
        Item savedItem = itemRepository.save(item1);
        long itemId = savedItem.getId();

        mvc.perform(get("/items/{itemId}", itemId)
                        .header(USER_ID, 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getItemsByUserId_shouldReturnUserItems() throws Exception {
        Item savedItem1 = itemRepository.save(item1);
        Item savedItem2 = itemRepository.save(item2);

        mvc.perform(get("/items")
                        .header(USER_ID, owner.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(savedItem1.getId()))
                .andExpect(jsonPath("$[1].id").value(savedItem2.getId()));
    }

    @Test
    void getItemsByUserId_whenUserHasNoItems() throws Exception {
        Item savedItem = itemRepository.save(item1);

        mvc.perform(get("/items")
                        .header(USER_ID, author.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getItemsByText_whenTextExists() throws Exception {
        Item savedItem1 = itemRepository.save(item1);
        Item savedItem2 = itemRepository.save(item2);

        mvc.perform(get("/items/search")
                        .header(USER_ID, owner.getId())
                        .param("text", "item")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value(savedItem1.getName()));

        mvc.perform(get("/items/search")
                        .header(USER_ID, owner.getId())
                        .param("text", "some text")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getItemsByText_whenEmptyText_thenReturnedEmptyList() throws Exception {
        mvc.perform(get("/items/search")
                        .header(USER_ID, owner.getId())
                        .param("text", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }


    @Test
    void addCommentTest() throws Exception {
        Item item = createItem("item", "description", owner, true);
        Item savedItem = itemRepository.save(item);
        Booking booking = createBooking(LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(3), item, author, BookingStatus.APPROVED);
        long itemId = savedItem.getId();

        CommentRequest comment = CommentRequest.builder().text("comment").build();

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(USER_ID, author.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("comment"))
                .andExpect(jsonPath("$.authorName").value(author.getName()))
                .andExpect(jsonPath("$.created").exists());
    }

    private User createUser(String name, String email) {
        User user = User.builder()
                .name(name)
                .email(email)
                .build();
        em.persist(user);
        return user;
    }

    private ItemDto createItemDto(String name, String description, Long requestId, boolean available) {
        return ItemDto.builder()
                .name(name)
                .description(description)
                .requestId(requestId)
                .available(available)
                .build();
    }

    private Item createItem(String name, String description, User owner, boolean available) {
        return Item.builder()
                .name(name)
                .description(description)
                .owner(owner)
                .available(available)
                .build();
    }

    private Booking createBooking(LocalDateTime start, LocalDateTime end, Item item, User booker, BookingStatus status) {
        Booking booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(status)
                .build();
        em.persist(booking);
        return booking;
    }
}