package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    /**
     * Получение списка вещей, принадлежащих пользователю
     *
     * @param ownerId - id пользователя
     * @return список вещей
     */
    List<Item> findByOwnerId(long ownerId);

    /**
     * Получение списка вещей по текстовому запросу
     *
     * @param text - текстовый запрос
     * @return список вещей
     */
    @Query("SELECT i FROM Item i " +
           "WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%', ?1, '%')) " +
           "   OR LOWER(i.description) LIKE LOWER(CONCAT('%', ?1, '%')))" +
           "   AND i.available = true")
    List<Item> findByText(String text);

    boolean existsByOwnerId(long ownerId);
}
