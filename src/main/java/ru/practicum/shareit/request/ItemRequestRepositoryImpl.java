package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ItemRequestRepositoryImpl implements ItemRequestRepository {

    /**
     * Метод-заглушка для возможности добавить информацию о запросе в Item
     */
    @Override
    public Optional<ItemRequest> getItemRequestById(long id) {
        User requestor = User.builder()
                .id(1L)
                .name("name")
                .email("mail@mail.ru")
                .build();
        ItemRequest request = new ItemRequest(1L, "description", requestor, LocalDateTime.now());
        return Optional.of(request);
    }
}
