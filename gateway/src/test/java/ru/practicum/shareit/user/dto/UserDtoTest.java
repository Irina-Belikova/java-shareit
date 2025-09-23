package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureJsonTesters
class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void serialized_UserDto() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("pochta@mail.com")
                .build();

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("pochta@mail.com");
    }

    @Test
    void deserialized_withId_UserDto() throws Exception {
        // CHECKSTYLE:OFF: RegexpSinglelineJava
        String request = """
                {
                "id": 1,
                "name": "Masha",
                "email": "pochta@mail.com"
                }
                """;
        // CHECKSTYLE:OFF: RegexpSinglelineJava

        UserDto userDto = json.parseObject(request);

        assertThat(userDto.getId()).isNull();
        assertThat(userDto.getName()).isEqualTo("Masha");
        assertThat(userDto.getEmail()).isEqualTo("pochta@mail.com");
    }

    @Test
    void hasEmailTest() {
        UserDto withEmail = UserDto.builder().email("pochta@mail.com").build();
        UserDto withoutEmail = UserDto.builder().email(null).build();
        UserDto withBlankEmail = UserDto.builder().email(" ").build();

        assertThat(withEmail.hasEmail()).isTrue();
        assertThat(withoutEmail.hasEmail()).isFalse();
        assertThat(withBlankEmail.hasEmail()).isFalse();
    }
}
