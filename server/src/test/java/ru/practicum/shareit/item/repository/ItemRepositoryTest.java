package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TestEntityManager em;

    private User owner;
    private Item item1;

    @BeforeEach
    void setup() {
        em.clear();
        itemRepository.deleteAll();

        owner = User.builder()
                .name("name1")
                .email("name1@mail.com")
                .build();
        em.persist(owner);

        item1 = Item.builder()
                .name("item-1")
                .description("description-1")
                .available(true)
                .owner(owner)
                .build();
    }

    @Test
    void findByOwnerIdTest() {
        Item savedItem = itemRepository.save(item1);

        List<Item> items = itemRepository.findByOwnerId(owner.getId());
        assertEquals(1, items.size());
        assertThat(items.getFirst()).isEqualTo(savedItem);

        List<Item> items2 = itemRepository.findByOwnerId(9L);
        assertEquals(0, items2.size());
    }

    @Test
    void findByTextTest() {
        Item savedItem = itemRepository.save(item1);

        List<Item> items = itemRepository.findByText("RIpT");

        assertEquals(1, items.size());
        assertThat(items.getFirst()).isEqualTo(savedItem);
    }

    @Test
    void existsByOwnerIdTest() {
        Item savedItem = itemRepository.save(item1);

        boolean isExists = itemRepository.existsByOwnerId(owner.getId());
        assertThat(isExists).isTrue();

        boolean isExists2 = itemRepository.existsByOwnerId(9L);
        assertThat(isExists2).isFalse();
    }
}
