package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository requestRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto, long userId) {
        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с таким id - %s не найден.", userId)));
        Item item = ItemMapper.mapToItem(itemDto, user);

        if (itemDto.getRequestId() != null) {
            addRequestToItem(itemDto.getRequestId(), item);
        }
        item = itemRepository.addItem(item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto updateItem(long itemId, ItemDto itemDto) {
        Item updateItem = itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с таким id - %s не найдена.", itemId)));

        if (itemDto.hasName()) {
            updateItem.setName(itemDto.getName());
        }

        if (itemDto.hasDescription()) {
            updateItem.setDescription(itemDto.getDescription());
        }

        if (itemDto.hasAvailable()) {
            updateItem.setAvailable(itemDto.getAvailable());
        }

        if (itemDto.getRequestId() != null) {
            addRequestToItem(itemDto.getRequestId(), updateItem);
        }
        updateItem = itemRepository.updateItem(updateItem);
        return ItemMapper.mapToItemDto(updateItem);
    }

    @Override
    public ItemDto getItemById(long itemId) {
        Item item = itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с таким id - %s не найдена.", itemId)));
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByUserId(long userId) {
        List<Item> userItems = itemRepository.getItemsByUserId(userId);
        return userItems.stream().map(ItemMapper::mapToItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsByText(String text) {
        List<Item> searchItems = itemRepository.getItemsByText(text);
        return searchItems.stream().map(ItemMapper::mapToItemDto).collect(Collectors.toList());
    }

    private void addRequestToItem(Long requestId, Item item) {
        ItemRequest request = requestRepository.getItemRequestById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос с таким id - %s не найден.", requestId)));
        item.setRequest(request);
    }
}
