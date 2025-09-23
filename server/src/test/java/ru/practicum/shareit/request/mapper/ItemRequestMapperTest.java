package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class ItemRequestMapperTest {

    @InjectMocks
    private ItemRequestMapperImpl requestMapper;

    @Test
    void mapToItemRequestForCreateTest() {
        User requestor = User.builder()
                .id(1L)
                .name("name")
                .email("pochta@mail.com")
                .build();
        ItemRequestRequest dto = ItemRequestRequest.builder().build();
        dto.setDescription("description");

        ItemRequest request = requestMapper.mapToItemRequestForCreate(dto, requestor);

        assertThat(request).isNotNull();
        assertThat(request.getRequestor()).isEqualTo(requestor);
        assertThat(request.getCreated()).isNotNull();
        assertThat(request.getDescription()).isEqualTo("description");
    }

    @Test
    void mapToItemRequestResponseTest() {
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
        User requestor = User.builder()
                .id(1L)
                .name("name")
                .email("pochta@mail.com")
                .build();
        LocalDateTime created = LocalDateTime.of(2024, 1, 15, 10, 30, 45);

        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(requestor)
                .created(created)
                .build();

        ItemRequestResponse response = requestMapper.mapToItemRequestResponse(request,items);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getDescription()).isEqualTo("description");
        assertThat(response.getCreated()).isEqualTo(created);
        assertThat(response.getItems()).isEqualTo(items);
    }
}