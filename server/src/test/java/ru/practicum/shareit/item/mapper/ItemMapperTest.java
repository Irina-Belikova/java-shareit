package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class ItemMapperTest {

    @InjectMocks
    private ItemMapperImpl itemMapper;

    @Test
    void mapToItemTest() {
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.com")
                .build();

        ItemDto itemDto = ItemDto.builder()
                .id(9L)
                .name("item")
                .description("description")
                .available(true)
                .requestId(2L)
                .build();

        Item item = itemMapper.mapToItem(itemDto, owner);

        assertThat(item.getId()).isNull();
        assertThat(item.getName()).isEqualTo("item");
        assertThat(item.getDescription()).isEqualTo("description");
        assertThat(item.getAvailable()).isTrue();
        assertThat(item.getOwner()).isEqualTo(owner);
        assertThat(item.getRequest()).isNull();
    }

    @Test
    void mapToItemDtoTest() {
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.com")
                .build();

        User requestor = User.builder()
                .id(2L)
                .name("Requestor")
                .email("requestor@mail.com")
                .build();

        ItemRequest request = ItemRequest.builder()
                .id(9L)
                .description("Need item")
                .requestor(requestor)
                .build();

        Item item = Item.builder()
                .id(2L)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner)
                .request(request)
                .build();

        ItemDto itemDto = itemMapper.mapToItemDto(item);

        assertThat(itemDto.getId()).isEqualTo(2L);
        assertThat(itemDto.getName()).isEqualTo("item");
        assertThat(itemDto.getDescription()).isEqualTo("description");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getRequestId()).isEqualTo(9L);
        assertThat(itemDto.getLastBooking()).isNull();
        assertThat(itemDto.getNextBooking()).isNull();
        assertThat(itemDto.getComments()).isNull();
    }

    @Test
    void updateItemFromDtoTest() {
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.com")
                .build();

        Item existingItem = Item.builder()
                .id(2L)
                .name("name-1")
                .description("description-1")
                .available(false)
                .owner(owner)
                .build();

        ItemDto updateDto = ItemDto.builder()
                .id(9L)
                .name("new name")
                .description("new description")
                .available(null)
                .requestId(3L)
                .build();

        itemMapper.updateItemFromDto(updateDto, existingItem);

        assertThat(existingItem.getId()).isEqualTo(2L);
        assertThat(existingItem.getName()).isEqualTo("new name");
        assertThat(existingItem.getDescription()).isEqualTo("new description");
        assertThat(existingItem.getAvailable()).isFalse();
        assertThat(existingItem.getOwner()).isEqualTo(owner);
        assertThat(existingItem.getRequest()).isNull();
    }

    @Test
    void mapToItemDtoList_whenListExists() {
        User owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.com")
                .build();

        Item item1 = Item.builder()
                .id(1L)
                .name("item-1")
                .description("description-1")
                .available(true)
                .owner(owner)
                .build();

        Item item2 = Item.builder()
                .id(2L)
                .name("item-2")
                .description("description-2")
                .available(false)
                .owner(owner)
                .build();

        List<Item> items = List.of(item1, item2);

        List<ItemDto> itemDtos = itemMapper.mapToItemDtoList(items);

        assertThat(itemDtos.size()).isEqualTo(2);
        assertThat(itemDtos.get(0).getId()).isEqualTo(1L);
        assertThat(itemDtos.get(0).getName()).isEqualTo("item-1");
        assertThat(itemDtos.get(1).getId()).isEqualTo(2L);
        assertThat(itemDtos.get(1).getName()).isEqualTo("item-2");
    }

    @Test
    void mapToItemDtoList_whenEmptyList() {
        List<ItemDto> itemDtos = itemMapper.mapToItemDtoList(List.of());

        assertThat(itemDtos.size()).isEqualTo(0);
    }
}