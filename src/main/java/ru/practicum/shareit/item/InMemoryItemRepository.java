package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ServerErrorException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InMemoryItemRepository implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private long itemId = 0;

    @Override
    public Item addItem(Item item) {
        try {
            item.setId(++itemId);
            items.put(item.getId(), item);
            return item;
        } catch (Exception e) {
            throw new ServerErrorException("Ошибка при сохранении новой вещи.");
        }
    }

    @Override
    public Item updateItem(Item item) {
        try {
            items.put(item.getId(), item);
        } catch (Exception e) {
            throw new ServerErrorException("Ошибка при обновлении данных вещи.");
        }
        return item;
    }

    @Override
    public Optional<Item> getItemById(long id) {
        try {
            return Optional.ofNullable(items.get(id));
        } catch (Exception e) {
            throw new ServerErrorException("Ошибка при поиске вещи.");
        }
    }

    @Override
    public List<Item> getItemsByUserId(long userId) {
        try {
            return items.values().stream()
                    .filter(item -> item.getOwner().getId() == userId)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ServerErrorException("Ошибка при поиске вещей пользователя.");
        }
    }

    @Override
    public List<Item> getItemsByText(String text) {
        String textLower = text.toLowerCase();
        String[] words = textLower.split(" ");
        try {
            return items.values().stream()
                    .filter(item -> item.getAvailable() == true)
                    .filter(item -> Arrays.stream(words).allMatch(word ->
                            item.getName().toLowerCase().contains(word) ||
                            item.getDescription().toLowerCase().contains(word)))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ServerErrorException("Ошибка при поиске вещей по текстовому описанию.");
        }
    }
}
