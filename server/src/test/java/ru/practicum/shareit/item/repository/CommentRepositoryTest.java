package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager em;

    private User author;
    private User owner;
    private Item item1;
    private Item item2;
    private Comment comment1;
    private Comment comment2;
    private Comment comment3;

    @BeforeEach
    void setup() {
        em.clear();
        commentRepository.deleteAll();

        author = createUser("author", "author@mail.com");
        owner = createUser("owner", "owner@mail.com");

        item1 = createItem("item-1", "description-1", owner, true);
        item2 = createItem("item-2", "description-2", owner, true);

        comment1 = createComment("comment-1", item1, author, LocalDateTime.now().minusDays(2));
        comment2 = createComment("comment-2", item1, author, LocalDateTime.now().minusDays(1));
        comment3 = createComment("comment-3", item2, author, LocalDateTime.now());
    }

    @Test
    void findByItemId_whenCommentsExists() {
        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);

        List<Comment> comments1 = commentRepository.findByItemId(item1.getId());
        List<Comment> comments2 = commentRepository.findByItemId(item2.getId());

        assertThat(comments1.size()).isEqualTo(2);
        assertThat(comments1.get(0).getText()).isEqualTo("comment-1");
        assertThat(comments1.get(1).getText()).isEqualTo("comment-2");

        assertThat(comments2.size()).isEqualTo(1);
        assertThat(comments2.get(0).getText()).isEqualTo("comment-3");
    }

    @Test
    void findByItemId_whenNoComments_thenReturnEmptyList() {
        long itemId = item1.getId();

        List<Comment> comments = commentRepository.findByItemId(itemId);

        assertThat(comments.size()).isEqualTo(0);
    }

    @Test
    void findByItemIdInOrderByCreatedDescTest() {
        Item item3 = createItem("item-3", "description-3", owner, true);
        Comment comment = createComment("comment-3", item3, author, LocalDateTime.now());
        commentRepository.save(comment);
        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);

        List<Comment> comments = commentRepository.findByItemIdInOrderByCreatedDesc(
                List.of(item1.getId(), item2.getId()));

        assertThat(comments.size()).isEqualTo(3);
        assertThat(comments.get(0).getText()).isEqualTo("comment-3");
        assertThat(comments.get(1).getText()).isEqualTo("comment-2");
        assertThat(comments.get(2).getText()).isEqualTo("comment-1");
    }


    @Test
    void getAllCommentsByItemId_whenExistsListItemId() {
        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);

        Map<Long, List<Comment>> comments = commentRepository.getAllCommentsByItemId(
                List.of(item1.getId(), item2.getId()));

        assertThat(comments.size()).isEqualTo(2);

        assertThat(comments.get(item1.getId()).size()).isEqualTo(2);
        assertThat(comments.get(item1.getId()).get(0).getText()).isEqualTo("comment-2");
        assertThat(comments.get(item1.getId()).get(1).getText()).isEqualTo("comment-1");

        assertThat(comments.get(item2.getId()).size()).isEqualTo(1);
        assertThat(comments.get(item2.getId()).get(0).getText()).isEqualTo("comment-3");
    }


    @Test
    void getAllCommentsByItemId_whenEmptyItemIdsList() {
        Map<Long, List<Comment>> comments = commentRepository.getAllCommentsByItemId(List.of());

        assertThat(comments.size()).isEqualTo(0);
    }

    @Test
    void saveTest() {
        Comment savedComment = commentRepository.save(comment1);

        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getId()).isNotNull();
        assertThat(savedComment.getText()).isEqualTo("comment-1");
        assertThat(commentRepository.count()).isEqualTo(1);
    }

    private User createUser(String name, String email) {
        User user = User.builder()
                .name(name)
                .email(email)
                .build();
        em.persist(user);
        return user;
    }

    private Item createItem(String name, String description, User owner, boolean available) {
        Item item = Item.builder()
                .name(name)
                .description(description)
                .owner(owner)
                .available(available)
                .build();
        em.persist(item);
        return item;
    }

    private Comment createComment(String text, Item item, User author, LocalDateTime created) {
        return Comment.builder()
                .text(text)
                .item(item)
                .author(author)
                .created(created)
                .build();
    }
}
