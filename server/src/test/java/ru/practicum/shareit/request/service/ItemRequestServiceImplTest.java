package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @InjectMocks
    private ItemRequestServiceImpl requestService;

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestMapper requestMapper;

    @Test
    void addRequest_whenValidData() {
        long requestorId = 1L;
        ItemRequestRequest requestDto = ItemRequestRequest.builder().build();
        User requestor = User.builder().id(requestorId).build();
        ItemRequest request = ItemRequest.builder().build();
        ItemRequest savedRequest = ItemRequest.builder().build();
        ItemRequestResponse responseDto = ItemRequestResponse.builder().build();

        when(userRepository.findById(requestorId)).thenReturn(Optional.of(requestor));
        when(requestMapper.mapToItemRequestForCreate(requestDto, requestor)).thenReturn(request);
        when(requestRepository.save(request)).thenReturn(savedRequest);
        when(requestMapper.mapToItemRequestResponse(savedRequest, new ArrayList<>())).thenReturn(responseDto);

        ItemRequestResponse actualResponse = requestService.addRequest(requestDto, requestorId);

        assertEquals(responseDto, actualResponse);

        verify(userRepository).findById(requestorId);
        verify(requestMapper).mapToItemRequestForCreate(requestDto, requestor);
        verify(requestRepository).save(request);
        verify(requestMapper).mapToItemRequestResponse(savedRequest, new ArrayList<>());
    }

    @Test
    void addRequest_whenFailUserId() {
        long requestorId = 1L;
        ItemRequestRequest requestDto = ItemRequestRequest.builder().build();

        when(userRepository.findById(requestorId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.addRequest(requestDto, requestorId));

        verify(userRepository).findById(requestorId);
        verify(requestMapper, never()).mapToItemRequestForCreate(any(), any());
        verify(requestRepository, never()).save(any());
        verify(requestMapper, never()).mapToItemRequestResponse(any(), any());
    }

    @Test
    void getRequestsByRequestorTest() {
        List<ItemRequestResponse> requests = List.of();
        long requestorId = 1L;
        when(requestRepository.findRequestsByRequestorId(requestorId)).thenReturn(requests);

        List<ItemRequestResponse> actualRequests = requestService.getRequestsByRequestor(requestorId);

        assertEquals(requests, actualRequests);
        verify(requestRepository).findRequestsByRequestorId(requestorId);
    }

    @Test
    void getRequestsByOtherRequestorTest() {
        List<ItemRequestResponse> requests = List.of();
        long requestorId = 1L;
        when(requestRepository.findRequestsByOtherRequestors(requestorId)).thenReturn(requests);

        List<ItemRequestResponse> actualRequests = requestService.getRequestsByOtherRequestor(requestorId);

        assertEquals(requests, actualRequests);
        verify(requestRepository).findRequestsByOtherRequestors(requestorId);
    }

    @Test
    void getRequestByIdTest() {
        long requestId = 1L;
        ItemRequestResponse response = ItemRequestResponse.builder().build();

        when(requestRepository.findByRequestId(requestId)).thenReturn(response);

        ItemRequestResponse actualResponse = requestService.getRequestById(requestId);

        assertEquals(response, actualResponse);
        verify(requestRepository).findByRequestId(requestId);
    }
}