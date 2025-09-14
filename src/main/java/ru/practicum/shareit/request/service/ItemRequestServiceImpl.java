package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper requestMapper;

    @Override
    @Transactional
    public ItemRequestResponse addRequest(ItemRequestRequest request, long requestorId) {
        User requestor = userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с таким id - %s не найден.", requestorId)));
        ItemRequest itemRequest = requestMapper.mapToItemRequestForCreate(request, requestor);
        itemRequest = requestRepository.save(itemRequest);
        return requestMapper.mapToItemRequestResponse(itemRequest, new ArrayList<>());
    }

    @Override
    public List<ItemRequestResponse> getRequestsByRequestor(long requestorId) {
        return requestRepository.findRequestsByRequestorId(requestorId);
    }

    @Override
    public List<ItemRequestResponse> getRequestsByOtherRequestor(long requestorId) {
        return requestRepository.findRequestsByOtherRequestors(requestorId);
    }

    @Override
    public ItemRequestResponse getRequestById(long requestId) {
        return requestRepository.findByRequestId(requestId);
    }
}
