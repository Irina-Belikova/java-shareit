package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    /**
     * Получение запроса по id
     * @param id - id запроса
     * @return Optional найденного или нет запроса
     */
    Optional<ItemRequest> searchById(long id);
}
