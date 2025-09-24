package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@SpringBootTest
@AutoConfigureJsonTesters
class CommentResponseTest {

    @Autowired
    private JacksonTester<CommentResponse> json;

    @Test
    void serialized_CommentResponse() throws Exception {
        LocalDateTime created = LocalDateTime.of(2024, 1, 15, 10, 30, 45);
        CommentResponse comment = CommentResponse.builder()
                .id(1L)
                .text("comment about item")
                .authorName("name")
                .created(created)
                .build();

        JsonContent<CommentResponse> result = json.write(comment);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("comment about item");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2024-01-15T10:30:45");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("name");
    }
}
