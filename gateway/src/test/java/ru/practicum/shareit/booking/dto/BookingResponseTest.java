package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingResponseTest {

    @Autowired
    private JacksonTester<BookingResponse> json;

    @Test
    void serialized_BookingResponseWithAllFields() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .requestId(100L)
                .lastBooking(LocalDateTime.of(2024, 1, 1, 10, 0))
                .nextBooking(LocalDateTime.of(2024, 12, 1, 12, 0))
                .comments(List.of(
                        CommentResponse.builder()
                                .id(1L)
                                .text("comment")
                                .authorName("author")
                                .created(LocalDateTime.of(2024, 6, 1, 14, 30))
                                .build()
                ))
                .build();

        UserDto booker = UserDto.builder()
                .id(2L)
                .name("Booker")
                .email("booker@example.com")
                .build();

        BookingResponse bookingResponse = BookingResponse.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 12, 1, 10, 0))
                .end(LocalDateTime.of(2024, 12, 1, 12, 0))
                .item(itemDto)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        JsonContent<BookingResponse> result = json.write(bookingResponse);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2024-12-01T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2024-12-01T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");

        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.item.requestId").isEqualTo(100);
        assertThat(result).extractingJsonPathStringValue("$.item.lastBooking").isEqualTo("2024-01-01T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.item.nextBooking").isEqualTo("2024-12-01T12:00:00");

        assertThat(result).extractingJsonPathNumberValue("$.item.comments[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.comments[0].text").isEqualTo("comment");
        assertThat(result).extractingJsonPathStringValue("$.item.comments[0].authorName").isEqualTo("author");
        assertThat(result).extractingJsonPathStringValue("$.item.comments[0].created").isEqualTo("2024-06-01T14:30:00");

        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("Booker");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("booker@example.com");
    }

    @Test
    void serialized_BookingResponseWithAnyFields() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .build();

        UserDto booker = UserDto.builder()
                .id(2L)
                .name("booker")
                .email("booker@example.com")
                .build();

        BookingResponse bookingResponse = BookingResponse.builder()
                .id(1L)
                .start(LocalDateTime.of(2024, 12, 1, 10, 0))
                .end(LocalDateTime.of(2024, 12, 1, 12, 0))
                .item(itemDto)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        JsonContent<BookingResponse> result = json.write(bookingResponse);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2024-12-01T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2024-12-01T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");

        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathValue("$.item.requestId").isNull();
        assertThat(result).extractingJsonPathValue("$.item.lastBooking").isNull();
        assertThat(result).extractingJsonPathValue("$.item.nextBooking").isNull();
        assertThat(result).extractingJsonPathValue("$.item.comments").isNull();

        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("booker");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("booker@example.com");
    }
}
