package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(ItemRequestRepositoryImpl.class)
class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRequestRepository requestRepository;

    private ItemRequest request;
    private ItemRequest request2;
    private User requestor;
    private User owner;
    private Item item;

    @BeforeEach
    void setup() {
        em.clear();
        requestRepository.deleteAll();

        requestor = createUser("requestor", "pochta@mail.com");
        request = createRequest("description", requestor, LocalDateTime.now().minusDays(1));
        request2 = createRequest("description2", requestor, LocalDateTime.now());
        owner = createUser("ownerItem", "email@mail.com");
    }

    @Test
    void saveTest() {
        ItemRequest savedRequest = requestRepository.save(request);

        assertThat(savedRequest.getId()).isNotNull();
        assertThat(savedRequest.getRequestor()).isEqualTo(requestor);
        assertThat(requestRepository.count()).isEqualTo(1);
    }

    @Test
    void findById_whenFound() {
        ItemRequest request2 = createRequest("description2", requestor, LocalDateTime.now());

        ItemRequest savedRequest = requestRepository.save(request);
        long id = savedRequest.getId();
        requestRepository.save(request2);

        Optional<ItemRequest> foundRequest = requestRepository.findById(id);

        assertThat(foundRequest).isPresent();
        assertThat(foundRequest.get().getId()).isEqualTo(id);
        assertThat(foundRequest.get().getRequestor()).isEqualTo(requestor);
    }

    @Test
    void findById_whenNotFound_returnEmpty() {
        Optional<ItemRequest> foundRequest = requestRepository.findById(1L);

        assertThat(foundRequest).isEmpty();
    }

    @Test
    void existsById_whenFound_returnTrue() {
        ItemRequest savedRequest = requestRepository.save(request);

        boolean isExists = requestRepository.existsById(savedRequest.getId());

        assertTrue(isExists);
    }

    @Test
    void existsById_whenNotFound_returnFalse() {
        boolean isExists = requestRepository.existsById(1L);

        assertFalse(isExists);
    }

    @Test
    void findByRequestId_whenFound() {
        ItemRequest savedRequest = requestRepository.save(request);
        item = createItem("item", owner, savedRequest);

        ItemRequestResponse response = requestRepository.findByRequestId(savedRequest.getId());

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(savedRequest.getId());
        assertThat(response.getItems().size()).isEqualTo(1);
    }

    @Test
    void findRequestsByRequestorId_whenFound_andNotFound() {
        ItemRequest savedRequest = requestRepository.save(request);
        ItemRequest savedRequest2 = requestRepository.save(request2);
        item = createItem("item", owner, savedRequest);

        List<ItemRequestResponse> result = requestRepository.findRequestsByRequestorId(requestor.getId());

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(1).getId()).isEqualTo(savedRequest2.getId());
        assertThat(result.get(0).getItems().size()).isEqualTo(1);

        List<ItemRequestResponse> result2 = requestRepository.findRequestsByRequestorId(requestor.getId() + 1);

        assertThat(result2.size()).isEqualTo(0);
    }

    @Test
    void findRequestByOtherRequestorTest() {
        User requestor2 = createUser("requestor2", "pochta2@mail.com");
        ItemRequest request3 = createRequest("description2", requestor2, LocalDateTime.now());
        ItemRequest savedRequest = requestRepository.save(request);
        ItemRequest savedRequest2 = requestRepository.save(request2);
        ItemRequest savedRequest3 = requestRepository.save(request3);
        item = createItem("item", owner, savedRequest);

        List<ItemRequestResponse> result = requestRepository.findRequestsByOtherRequestors(requestor2.getId());

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(1).getId()).isEqualTo(savedRequest2.getId());
        assertThat(result.get(0).getItems().size()).isEqualTo(1);
    }

    private User createUser(String name, String email) {
        User user = User.builder().name(name).email(email).build();
        em.persist(user);
        return user;
    }

    private ItemRequest createRequest(String description, User requestor, LocalDateTime created) {
        return  ItemRequest.builder()
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