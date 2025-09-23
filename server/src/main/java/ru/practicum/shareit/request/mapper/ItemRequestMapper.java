package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemRequestMapper {

    @Mapping(target = "id", ignore = true)
    default ItemRequest mapToItemRequestForCreate(ItemRequestRequest itemRequest, User requestor) {
        return ItemRequest.builder()
                .description(itemRequest.getDescription())
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();
    }

    ItemRequestResponse mapToItemRequestResponse(ItemRequest itemRequest, List<RequestItemDto> items);
}
