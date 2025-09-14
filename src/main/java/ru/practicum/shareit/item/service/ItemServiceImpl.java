package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository requestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemDto addItem(ItemDto itemDto, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с таким id - %s не найден.", userId)));
        Item item = itemMapper.mapToItem(itemDto, user);

        if (itemDto.getRequestId() != null) {
            addRequestToItem(itemDto.getRequestId(), item);
        }
        item = itemRepository.save(item);
        return itemMapper.mapToItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(long itemId, ItemDto itemDto) {
        Item updateItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с таким id - %s не найдена.", itemId)));
        itemMapper.updateItemFromDto(itemDto, updateItem);

        if (itemDto.getRequestId() != null) {
            addRequestToItem(itemDto.getRequestId(), updateItem);
        }
        return itemMapper.mapToItemDto(updateItem);
    }

    @Override
    public ItemDto getItemById(long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с таким id - %s не найдена.", itemId)));
        ItemDto itemDto = itemMapper.mapToItemDto(item);
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
                    ItemDto dto = itemMapper.mapToItemDto(item);
                    dto.setLastBooking(lastBookings.getOrDefault(dto.getId(), null));
                    dto.setNextBooking(nextBookings.getOrDefault(dto.getId(), null));
                    List<Comment> itemComments = comments.getOrDefault(item.getId(), new ArrayList<>());
                    dto.setComments(commentMapper.mapToCommentResponseList(itemComments));
                    return dto;
                }).toList();
    }

    @Override
    public List<ItemDto> getItemsByText(String text) {
        List<Item> searchItems = itemRepository.findByText(text);
        return itemMapper.mapToItemDtoList(searchItems);
    }

    @Override
    @Transactional
    public CommentResponse addComment(long authorId, long itemId, CommentRequest text) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с таким id - %s не найден.", authorId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с таким id - %s не найдена.", itemId)));
        Comment comment = commentMapper.mapToCommentForCreate(text, item, author);
        comment = commentRepository.save(comment);
        return commentMapper.mapToCommentResponse(comment);
    }

    private void addRequestToItem(Long requestId, Item item) {
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос с таким id - %s не найден.", requestId)));
        item.setRequest(request);
    }

    private void addCommentsToItemDto(ItemDto itemDto) {
        List<Comment> comments = commentRepository.findByItemId(itemDto.getId());
        itemDto.setComments(commentMapper.mapToCommentResponseList(comments));
    }
}
