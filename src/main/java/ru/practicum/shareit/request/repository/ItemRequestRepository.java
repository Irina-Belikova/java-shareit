package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    /**
     * Получение запроса по id
     * @param id - id запроса
     * @return Optional найденного или нет запроса
     */
    Optional<ItemRequest> searchById(long id);
}
