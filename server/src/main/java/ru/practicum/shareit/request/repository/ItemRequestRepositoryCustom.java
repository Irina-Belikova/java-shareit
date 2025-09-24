package ru.practicum.shareit.request.repository;

import ru.practicum.shareit.request.dto.ItemRequestResponse;

import java.util.List;

public interface ItemRequestRepositoryCustom {

    List<ItemRequestResponse> findRequestsByRequestorId(long requestorId);

    List<ItemRequestResponse> findRequestsByOtherRequestors(long requestorId);

    ItemRequestResponse findByRequestId(long requestId);
}
