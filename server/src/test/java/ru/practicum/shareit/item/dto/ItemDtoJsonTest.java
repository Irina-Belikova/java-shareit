package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@AutoConfigureJsonTesters
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void serialized_ItemDto() throws Exception {
        LocalDateTime lastBooking = LocalDateTime.of(2024, 1, 15, 10, 30, 45);
        LocalDateTime nextBooking = LocalDateTime.of(2024, 2, 20, 12, 30, 45);
        LocalDateTime createdResponse = LocalDateTime.of(2024, 1, 5, 10, 10, 45);

        List<CommentResponse> comments = List.of(
                CommentResponse.builder()
                .id(9L)
                .text("comment")
                .authorName("name")
                .created(createdResponse)
                .build()
        );

        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .requestId(2L)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();

        JsonContent<ItemDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking").isEqualTo("2024-01-15T10:30:45");
        assertThat(result).extractingJsonPathStringValue("$.nextBooking").isEqualTo("2024-02-20T12:30:45");
        assertThat(result).extractingJsonPathArrayValue("$.comments").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(9);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("comment");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created").isEqualTo("2024-01-05T10:10:45");
    }

    @Test
    void deserialized_ItemDto() throws Exception {
        // CHECKSTYLE:OFF: RegexpSinglelineJava
        String content = """
        {
            "id": 1,
            "name": "item",
            "description": "description",
            "available": true,
            "requestId": 2,
            "lastBooking": "2024-01-15T10:30:45",
            "nextBooking": "2024-02-20T12:30:45",
            "comments": [
                {
                    "id": 9,
                    "text": "comment-9",
                    "authorName": "name",
                    "created": "2024-01-05T10:10:45"
                },
                {
                    "id": 12,
                    "text": "comment-12",
                    "authorName": "Kot",
                    "created": "2024-01-14T15:30:00"
                }
            ]
        }
        """;
        // CHECKSTYLE:OFF: RegexpSinglelineJava

        ItemDto itemDto = json.parseObject(content);

        assertThat(itemDto.getId()).isNull();
        assertThat(itemDto.getName()).isEqualTo("item");
        assertThat(itemDto.getDescription()).isEqualTo("description");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getRequestId()).isEqualTo(2L);
        assertThat(itemDto.getLastBooking()).isEqualTo(LocalDateTime.of(2024, 1, 15, 10, 30, 45));
        assertThat(itemDto.getNextBooking()).isEqualTo(LocalDateTime.of(2024, 2, 20, 12, 30, 45));
        assertThat(itemDto.getComments()).hasSize(2);

        CommentResponse firstComment = itemDto.getComments().get(0);
        assertThat(firstComment.getId()).isEqualTo(9L);
        assertThat(firstComment.getText()).isEqualTo("comment-9");
        assertThat(firstComment.getAuthorName()).isEqualTo("name");
        assertThat(firstComment.getCreated()).isEqualTo(LocalDateTime.of(2024, 1, 5, 10, 10, 45));

        CommentResponse secondComment = itemDto.getComments().get(1);
        assertThat(secondComment.getId()).isEqualTo(12L);
        assertThat(secondComment.getText()).isEqualTo("comment-12");
        assertThat(secondComment.getAuthorName()).isEqualTo("Kot");
        assertThat(secondComment.getCreated()).isEqualTo(LocalDateTime.of(2024, 1, 14, 15, 30, 0));
    }
}