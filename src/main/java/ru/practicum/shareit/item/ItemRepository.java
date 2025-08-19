package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    /**
     * Добавление новой вещи в базу
     * @param item - объект новой вещи
     * @return объект Item
     */
    Item addItem(Item item);

    /**
     * Обновление данных о вещи
     * @param item - объект с обновлёнными данными
     * @return обновленный объект Item
     */
    Item updateItem(Item item);

    /**
     * Получение вещи по её id
     * @param id - id вещи
     * @return Optional найденной или нет вещи
     */
    Optional<Item> getItemById(long id);

    /**
     * Получение списка вещей, принадлежащих пользователю
     * @param userId - id пользователя
     * @return список вещей
     */
    List<Item> getItemsByUserId(long userId);

    /**
     * Получение списка вещей по текстовому запросу
     * @param text - текстовый запрос
     * @return список вещей
     */
    List<Item> getItemsByText(String text);
}
