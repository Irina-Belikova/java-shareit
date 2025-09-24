package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.CommentResponse;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl service;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private CommentMapper commentMapper;

    @Test
    void addItem_whenValidDataWithoutRequestId() {
        long userId = 1L;
        ItemDto dto = ItemDto.builder().build();
        User user = User.builder().build();
        Item item = Item.builder().build();
        Item savedItem = Item.builder().build();
        ItemDto responseDto = ItemDto.builder().build();

        ArgumentCaptor<Item> itemCaptor = ArgumentCaptor.forClass(Item.class);


        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemMapper.mapToItem(dto, user)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(savedItem);
        when(itemMapper.mapToItemDto(itemCaptor.capture())).thenReturn(responseDto);

        ItemDto actualDto = service.addItem(dto, userId);
        Item itemToMap = itemCaptor.getValue();

        assertEquals(responseDto, actualDto);
        assertEquals(savedItem, itemToMap);

        verify(userRepository).findById(userId);
        verify(itemMapper).mapToItem(dto, user);
        verify(itemRepository).save(item);
        verify(itemMapper).mapToItemDto(any());
        verify(requestRepository, never()).findById(anyLong());
    }

    @Test
    void addItem_whenValidDataAndRequestId() {
        long userId = 1L;
        long id = 2L;
        ItemDto dto = ItemDto.builder().requestId(id).build();
        User user = User.builder().build();
        Item item = Item.builder().build();
        Item savedItem = Item.builder().build();
        ItemDto responseDto = ItemDto.builder().build();
        ItemRequest request = ItemRequest.builder().build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemMapper.mapToItem(dto, user)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(savedItem);
        when(itemMapper.mapToItemDto(savedItem)).thenReturn(responseDto);
        when(requestRepository.findById(id)).thenReturn(Optional.of(request));

        ItemDto actualDto = service.addItem(dto, userId);

        assertEquals(responseDto, actualDto);

        verify(requestRepository).findById(id);
    }

    @Test
    void updateItemTest() {
        long itemId = 1L;
        long id = 2L;
        ItemDto dto = ItemDto.builder().requestId(id).build();
        Item item = Item.builder().build();
        ItemDto responseDto = ItemDto.builder().build();
        ItemRequest request = ItemRequest.builder().build();


        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        doNothing().when(itemMapper).updateItemFromDto(dto, item);
        when(itemMapper.mapToItemDto(any())).thenReturn(responseDto);
        when(requestRepository.findById(id)).thenReturn(Optional.of(request));

        ItemDto actualDto = service.updateItem(itemId, dto);

        assertEquals(responseDto, actualDto);

        verify(itemRepository).findById(itemId);
        verify(itemMapper).updateItemFromDto(dto, item);
        verify(itemMapper).mapToItemDto(any());
        verify(requestRepository).findById(id);
    }

    @Test
    void getItemById_returnedItemWithBookingsAndComments() {
        long itemId = 1L;
        Item item = Item.builder().id(itemId).build();
        ItemDto itemDto = ItemDto.builder().id(itemId).build();
        LocalDateTime lastBooking = LocalDateTime.now().minusDays(1);
        LocalDateTime nextBooking = LocalDateTime.now().plusDays(1);
        List<Comment> comments = List.of(Comment.builder().build());
        List<CommentResponse> commentResponses = List.of(CommentResponse.builder().build());

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.mapToItemDto(item)).thenReturn(itemDto);
        when(bookingRepository.getLastBookingByItemId(eq(itemId), any(LocalDateTime.class))).thenReturn(lastBooking);
        when(bookingRepository.getNextBookingByItemId(eq(itemId), any(LocalDateTime.class))).thenReturn(nextBooking);
        when(commentRepository.findByItemId(itemId)).thenReturn(comments);
        when(commentMapper.mapToCommentResponseList(comments)).thenReturn(commentResponses);

        ItemDto result = service.getItemById(itemId);

        assertThat(result).isEqualTo(itemDto);
        assertThat(result.getLastBooking()).isEqualTo(lastBooking);
        assertThat(result.getNextBooking()).isEqualTo(nextBooking);
        assertThat(result.getComments()).isEqualTo(commentResponses);

        verify(itemRepository).findById(itemId);
        verify(itemMapper).mapToItemDto(item);
        verify(bookingRepository).getLastBookingByItemId(eq(itemId), any(LocalDateTime.class));
        verify(bookingRepository).getNextBookingByItemId(eq(itemId), any(LocalDateTime.class));
        verify(commentRepository).findByItemId(itemId);
        verify(commentMapper).mapToCommentResponseList(comments);
    }

    @Test
    void getItemById_whenItemNotFound_thenThrownNotFoundException() {
        long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getItemById(itemId));

        verify(itemRepository).findById(itemId);
        verifyNoInteractions(itemMapper, bookingRepository, commentRepository, commentMapper);
    }

    @Test
    void getItemsByUserId_returnedItemsWithBookingsAndComments() {
        long userId = 1L;
        Item item1 = Item.builder().id(1L).build();
        Item item2 = Item.builder().id(2L).build();
        List<Item> userItems = List.of(item1, item2);
        List<Long> itemIds = List.of(1L, 2L);

        ItemDto dto1 = ItemDto.builder().id(1L).build();
        ItemDto dto2 = ItemDto.builder().id(2L).build();

        LocalDateTime lastBooking1 = LocalDateTime.now().minusDays(1);
        LocalDateTime nextBooking2 = LocalDateTime.now().plusDays(1);
        Map<Long, LocalDateTime> lastBookings = Map.of(1L, lastBooking1);
        Map<Long, LocalDateTime> nextBookings = Map.of(2L, nextBooking2);

        List<Comment> comments1 = List.of(Comment.builder().build());
        List<CommentResponse> commentResponses1 = List.of(CommentResponse.builder().build());
        Map<Long, List<Comment>> comments = Map.of(1L, comments1);

        when(itemRepository.findByOwnerId(userId)).thenReturn(userItems);
        when(bookingRepository.getAllLastBookingsByItemIds(eq(itemIds), any(LocalDateTime.class))).thenReturn(lastBookings);
        when(bookingRepository.getAllNextBookingsByItemIds(eq(itemIds), any(LocalDateTime.class))).thenReturn(nextBookings);
        when(commentRepository.getAllCommentsByItemId(itemIds)).thenReturn(comments);
        when(itemMapper.mapToItemDto(item1)).thenReturn(dto1);
        when(itemMapper.mapToItemDto(item2)).thenReturn(dto2);
        when(commentMapper.mapToCommentResponseList(comments1)).thenReturn(commentResponses1);

        List<ItemDto> result = service.getItemsByUserId(userId);

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getLastBooking()).isEqualTo(lastBooking1);
        assertThat(result.get(0).getNextBooking()).isNull();
        assertThat(result.get(0).getComments()).isEqualTo(commentResponses1);
        assertThat(result.get(1).getLastBooking()).isNull();
        assertThat(result.get(1).getNextBooking()).isEqualTo(nextBooking2);
        assertThat(result.get(1).getComments().size()).isEqualTo(0);

        verify(itemRepository).findByOwnerId(userId);
        verify(bookingRepository).getAllLastBookingsByItemIds(eq(itemIds), any(LocalDateTime.class));
        verify(bookingRepository).getAllNextBookingsByItemIds(eq(itemIds), any(LocalDateTime.class));
        verify(commentRepository).getAllCommentsByItemId(itemIds);
        verify(itemMapper, times(2)).mapToItemDto(any(Item.class));
        verify(commentMapper).mapToCommentResponseList(comments1);
    }

    @Test
    void getItemsByUserId_whenNoItems_thenReturnedEmptyList() {
        long userId = 1L;
        when(itemRepository.findByOwnerId(userId)).thenReturn(List.of());

        List<ItemDto> result = service.getItemsByUserId(userId);

        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    void getItemsByText_whenTextExists() {
        String text = "item";
        Item item1 = Item.builder().id(1L).name("namedOfItem").build();
        Item item2 = Item.builder().id(2L).name("item for Kot").build();
        List<Item> searchItems = List.of(item1, item2);

        ItemDto dto1 = ItemDto.builder().id(1L).build();
        ItemDto dto2 = ItemDto.builder().id(2L).build();
        List<ItemDto> dtos = List.of(dto1, dto2);

        when(itemRepository.findByText(text)).thenReturn(searchItems);
        when(itemMapper.mapToItemDtoList(searchItems)).thenReturn(dtos);

        List<ItemDto> result = service.getItemsByText(text);

        assertEquals(dtos, result);

        verify(itemRepository).findByText(text);
        verify(itemMapper).mapToItemDtoList(searchItems);
    }

    @Test
    void addComment_withValidData() {
        long authorId = 1L;
        long itemId = 2L;
        CommentRequest commentRequest = CommentRequest.builder().build();

        User author = User.builder().id(authorId).build();
        Item item = Item.builder().id(itemId).build();
        Comment comment = Comment.builder().build();
        Comment savedComment = Comment.builder().build();
        CommentResponse commentResponse = CommentResponse.builder().build();

        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentMapper.mapToCommentForCreate(commentRequest, item, author)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(savedComment);
        when(commentMapper.mapToCommentResponse(savedComment)).thenReturn(commentResponse);

        CommentResponse result = service.addComment(authorId, itemId, commentRequest);

        assertThat(result).isEqualTo(commentResponse);

        verify(userRepository).findById(authorId);
        verify(itemRepository).findById(itemId);
        verify(commentMapper).mapToCommentForCreate(commentRequest, item, author);
        verify(commentRepository).save(comment);
        verify(commentMapper).mapToCommentResponse(savedComment);
    }

    @Test
    void addComment_whenAuthorNotFound_thenThrownNotFoundException() {
        long authorId = 9L;
        long itemId = 2L;
        CommentRequest commentRequest = CommentRequest.builder().build();

        when(userRepository.findById(authorId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.addComment(authorId, itemId, commentRequest));

        verify(userRepository).findById(authorId);
        verifyNoInteractions(itemRepository, commentMapper, commentRepository);
    }

    @Test
    void addComment_whenItemNotFound_thenThrownNotFoundException() {
        long authorId = 1L;
        long itemId = 9L;
        CommentRequest commentRequest = CommentRequest.builder().build();
        User author = User.builder().id(authorId).build();

        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.addComment(authorId, itemId, commentRequest));

        verify(userRepository).findById(authorId);
        verify(itemRepository).findById(itemId);
        verifyNoInteractions(commentMapper, commentRepository);
    }
}