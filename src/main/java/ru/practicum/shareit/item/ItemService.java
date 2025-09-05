package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    /**
     * Добавление новой вещи в базу
     * @param itemDto - dto-объект новой вещи
     * @param userId  - id пользователя, который добавляет новую вещь (хозяин вещи)
     * @return dto-объект
     */
    ItemDto addItem(ItemDto itemDto, long userId);

    /**
     * Обновление данных о вещи
     * @param itemId  - id вещи, которая будет обновляться
     * @param itemDto - dto-объект с обновляемыми данными
     * @return обновленный dto-объект
     */
    ItemDto updateItem(long itemId, ItemDto itemDto);

    /**
     * Получение вещи по её id
     * @param itemId - id вещи
     * @return dto-объект
     */
    ItemDto getItemById(long itemId);

    /**
     * Получение списка вещей, принадлежащих пользователю
     * @param userId - id пользователя
     * @return список dto-вещей
     */
    List<ItemDto> getItemsByUserId(long userId);

    /**
     * Получение списка вещей по текстовому запросу
     * @param text - текстовый запрос
     * @return список dto-вещей
     */
    List<ItemDto> getItemsByText(String text);

    CommentResponse addComment(long authorId, long itemId, CommentRequest comment);
}
