package ru.practicum.shareit.request;

import java.util.Optional;

public interface ItemRequestRepository {

    /**
     * Получение запроса по id
     * @param id - id запроса
     * @return Optional найденного или нет запроса
     */
    Optional<ItemRequest> getItemRequestById(long id);
}
