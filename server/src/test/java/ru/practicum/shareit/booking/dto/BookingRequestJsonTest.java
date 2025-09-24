package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureJsonTesters
class BookingRequestJsonTest {

    @Autowired
    private JacksonTester<BookingRequest> json;

    @Test
    void deserialized_BookingRequestWithAllFields() throws IOException {
        // CHECKSTYLE:OFF: RegexpSinglelineJava
        String jsonContent = """
            {
                "start": "2024-12-01T10:00:00",
                "end": "2024-12-01T12:00:00",
                "itemId": 1,
                "bookerId": 2,
                "status": "WAITING"
            }
            """;
        // CHECKSTYLE:OFF: RegexpSinglelineJava

        BookingRequest bookingRequest = json.parseObject(jsonContent);

        assertThat(bookingRequest).isNotNull();
        assertThat(bookingRequest.getStart()).isEqualTo(LocalDateTime.of(2024, 12, 1, 10, 0));
        assertThat(bookingRequest.getEnd()).isEqualTo(LocalDateTime.of(2024, 12, 1, 12, 0));
        assertThat(bookingRequest.getItemId()).isEqualTo(1L);
        assertThat(bookingRequest.getBookerId()).isEqualTo(2L);
        assertThat(bookingRequest.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void deserializedWithNullStatus() throws IOException {
        // CHECKSTYLE:OFF: RegexpSinglelineJava
        String jsonContent = """
            {
                "start": "2024-12-01T10:00:00",
                "end": "2024-12-01T12:00:00",
                "itemId": 1,
                "bookerId": 2
            }
            """;
        // CHECKSTYLE:OFF: RegexpSinglelineJava

        BookingRequest bookingRequest = json.parseObject(jsonContent);

        assertThat(bookingRequest).isNotNull();
        assertThat(bookingRequest.getStatus()).isNull();
    }
}
