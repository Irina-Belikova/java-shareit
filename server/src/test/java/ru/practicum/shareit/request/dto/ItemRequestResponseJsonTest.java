package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.RequestItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@AutoConfigureJsonTesters
class ItemRequestResponseJsonTest {

    @Autowired
    private JacksonTester<ItemRequestResponse> json;

    @Test
    void serialized_ItemRequestResponse() throws Exception {
        LocalDateTime created = LocalDateTime.of(2024, 1, 15, 10, 30, 45);
        List<RequestItemDto> items = List.of(
                RequestItemDto.builder()
                        .id(1L)
                        .name("Item1")
                        .ownerId(10L)
                        .build(),
                RequestItemDto.builder()
                        .id(2L)
                        .name("Item2")
                        .ownerId(20L)
                        .build()
        );

        ItemRequestResponse response = ItemRequestResponse.builder()
                .id(123L)
                .description("запрос на вещь")
                .created(created)
                .items(items)
                .build();

        JsonContent<ItemRequestResponse> result = json.write(response);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(123);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("запрос на вещь");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2024-01-15T10:30:45");
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(2);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Item1");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].ownerId").isEqualTo(10);
        assertThat(result).extractingJsonPathNumberValue("$.items[1].id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.items[1].name").isEqualTo("Item2");
        assertThat(result).extractingJsonPathNumberValue("$.items[1].ownerId").isEqualTo(20);
    }
}
