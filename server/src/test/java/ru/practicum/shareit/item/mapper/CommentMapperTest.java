package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class CommentMapperTest {

    @InjectMocks
    private CommentMapperImpl commentMapper;

    @Test
    void mapToCommentForCreateTest() {
        CommentRequest commentRequest = CommentRequest.builder().text("comment").build();

        User author = User.builder()
                .id(1L)
                .name("author")
                .email("author@mail.com")
                .build();

        Item item = Item.builder()
                .id(10L)
                .name("item")
                .description("description")
                .available(true)
                .build();

        Comment comment = commentMapper.mapToCommentForCreate(commentRequest, item, author);

        assertThat(comment.getText()).isEqualTo("comment");
        assertThat(comment.getItem()).isEqualTo(item);
        assertThat(comment.getAuthor()).isEqualTo(author);
        assertThat(comment.getCreated()).isNotNull();
        assertThat(comment.getCreated()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void mapToCommentResponseTest() {
        User author = User.builder()
                .id(1L)
                .name("author")
                .email("author@mail.com")
                .build();

        Item item = Item.builder()
                .id(2L)
                .name("item")
                .description("description")
                .available(true)
                .build();

        Comment comment = Comment.builder()
                .id(9L)
                .text("comment")
                .item(item)
                .author(author)
                .created(LocalDateTime.of(2024, 1, 15, 10, 30, 45))
                .build();

        CommentResponse response = commentMapper.mapToCommentResponse(comment);

        assertThat(response.getId()).isEqualTo(9L);
        assertThat(response.getText()).isEqualTo("comment");
        assertThat(response.getAuthorName()).isEqualTo("author");
        assertThat(response.getCreated()).isEqualTo(LocalDateTime.of(2024, 1, 15, 10, 30, 45));
    }

    @Test
    void mapToCommentResponseListTest() {
        User author = User.builder()
                .id(9L)
                .name("author")
                .email("author@mail.com")
                .build();

        Comment comment1 = Comment.builder()
                .id(1L)
                .text("comment-1")
                .author(author)
                .created(LocalDateTime.of(2024, 1, 15, 10, 30, 45))
                .build();

        Comment comment2 = Comment.builder()
                .id(2L)
                .text("comment-2")
                .author(author)
                .created(LocalDateTime.of(2024, 1, 16, 12, 0, 0))
                .build();

        List<Comment> comments = List.of(comment1, comment2);

        List<CommentResponse> responses = commentMapper.mapToCommentResponseList(comments);

        assertThat(responses.size()).isEqualTo(2);
        assertThat(responses.get(0).getId()).isEqualTo(1L);
        assertThat(responses.get(0).getText()).isEqualTo("comment-1");
        assertThat(responses.get(0).getAuthorName()).isEqualTo("author");

        assertThat(responses.get(1).getId()).isEqualTo(2L);
        assertThat(responses.get(1).getText()).isEqualTo("comment-2");
        assertThat(responses.get(1).getAuthorName()).isEqualTo("author");
    }
}