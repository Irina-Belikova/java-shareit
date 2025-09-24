package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;


import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@AutoConfigureJsonTesters
class RequestItemDtoJsonTest {

    @Autowired
    private JacksonTester<RequestItemDto> json;

    @Test
    void serialized_RequestItemDto() throws Exception {
        RequestItemDto dto = RequestItemDto.builder()
                .id(1L)
                .name("item")
                .ownerId(2L)
                .build();

        JsonContent<RequestItemDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item");
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(2);
    }
}
