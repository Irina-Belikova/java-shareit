package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository requestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto addItem(ItemDto itemDto, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с таким id - %s не найден.", userId)));
        Item item = ItemMapper.mapToItem(itemDto, user);

        if (itemDto.getRequestId() != null) {
            addRequestToItem(itemDto.getRequestId(), item);
        }
        item = itemRepository.save(item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(long itemId, ItemDto itemDto) {
        Item updateItem = itemRepository.findById(itemId)
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
        updateItem = itemRepository.save(updateItem);
        return ItemMapper.mapToItemDto(updateItem);
    }

    @Override
    public ItemDto getItemById(long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с таким id - %s не найдена.", itemId)));
        ItemDto itemDto = ItemMapper.mapToItemDto(item);
        itemDto.setLastBooking(bookingRepository.getLastBookingByItemId(itemId, LocalDateTime.now()));
        itemDto.setNextBooking(bookingRepository.getNextBookingByItemId(itemId, LocalDateTime.now()));
        addCommentsToItemDto(itemDto);
        return itemDto;
    }

    @Override
    public List<ItemDto> getItemsByUserId(long userId) {
        List<Item> userItems = itemRepository.findByOwnerId(userId);
        List<Long> itemIds = userItems.stream().map(Item::getId).toList();
        Map<Long, LocalDateTime> lastBookings = bookingRepository.getAllLastBookingsByItemIds(itemIds, LocalDateTime.now());
        Map<Long, LocalDateTime> nextBookings = bookingRepository.getAllNextBookingsByItemIds(itemIds, LocalDateTime.now());
        Map<Long, List<Comment>> comments = commentRepository.getAllCommentsByItemId(itemIds);

        return userItems.stream()
                .map(item -> {
                    ItemDto dto = ItemMapper.mapToItemDto(item);
                    dto.setLastBooking(lastBookings.getOrDefault(dto.getId(), null));
                    dto.setNextBooking(nextBookings.getOrDefault(dto.getId(), null));
                    List<Comment> itemComments = comments.getOrDefault(item.getId(), new ArrayList<>());
                    dto.setComments(itemComments.stream().map(CommentMapper::mapToCommentResponse).toList());
                    return dto;
                }).toList();
    }

    @Override
    public List<ItemDto> getItemsByText(String text) {
        List<Item> searchItems = itemRepository.findByText(text);
        return searchItems.stream().map(ItemMapper::mapToItemDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentResponse addComment(long authorId, long itemId, CommentRequest text) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с таким id - %s не найден.", authorId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с таким id - %s не найдена.", itemId)));
        Comment comment = CommentMapper.mapToCommentForCreate(text.getText(), item, author);
        comment = commentRepository.save(comment);
        return CommentMapper.mapToCommentResponse(comment);
    }

    private void addRequestToItem(Long requestId, Item item) {
        ItemRequest request = requestRepository.searchById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос с таким id - %s не найден.", requestId)));
        item.setRequest(request);
    }

    private void addCommentsToItemDto(ItemDto itemDto) {
        List<Comment> comments = commentRepository.findByItemId(itemDto.getId());
        itemDto.setComments(comments.stream().map(CommentMapper::mapToCommentResponse).collect(Collectors.toList()));
    }
}
