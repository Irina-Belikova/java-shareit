package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;

import java.util.List;

public interface ItemRequestService {

    ItemRequestResponse addRequest(ItemRequestRequest request, long requestorId);

    List<ItemRequestResponse> getRequestsByRequestor(long requestorId);

    List<ItemRequestResponse> getRequestsByOtherRequestor(long requestorId);

    ItemRequestResponse getRequestById(long requestId);
}
