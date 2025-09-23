package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Test
    void createUserTest() {
        User userToSave = User.builder().build();
        UserDto dtoToSave = UserDto.builder().build();
        User newUser = User.builder().build();
        UserDto newDto = UserDto.builder().build();
        when(userMapper.mapToUser(dtoToSave)).thenReturn(userToSave);
        when(userRepository.save(userToSave)).thenReturn(newUser);
        when(userMapper.mapToUserDto(newUser)).thenReturn(newDto);

        UserDto actualDto = userService.createUser(dtoToSave);

        assertEquals(newDto, actualDto);
        verify(userMapper).mapToUser(dtoToSave);
        verify(userRepository).save(userToSave);
        verify(userMapper).mapToUserDto(newUser);
    }

    @Test
    void updateUser_whenUserFound_thenReturnedUpdateDto() {
        long userId = 1L;
        User existUser = User.builder().build();
        UserDto updateDto = UserDto.builder().build();
        UserDto newDto = UserDto.builder().build();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existUser));
        doNothing().when(userMapper).updateUserFromDto(eq(updateDto), userCaptor.capture());
        when(userMapper.mapToUserDto(existUser)).thenReturn(newDto);

        UserDto actualDto = userService.updateUser(userId, updateDto);
        User userToUpdate = userCaptor.getValue();

        assertEquals(newDto, actualDto);
        assertEquals(existUser, userToUpdate);
        InOrder inOrder = inOrder(userRepository, userMapper);
        inOrder.verify(userRepository).findById(userId);
        inOrder.verify(userMapper).updateUserFromDto(any(), any());
        inOrder.verify(userMapper).mapToUserDto(any());
    }

    @Test
    void updateUser_whenUserNotFound_thenThrownNotFoundException() {
        long userId = 1L;
        UserDto updateDto = UserDto.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(userId, updateDto));

        verify(userRepository).findById(userId);
        verify(userMapper, never()).updateUserFromDto(any(), any());
        verify(userMapper, never()).mapToUserDto(any());
    }

    @Test
    void getUserById_whenUserFound_thenReturnedUserDto() {
        long userId = 1L;
        User expectedUser = User.builder().build();
        UserDto expectedDto = UserDto.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
        when(userMapper.mapToUserDto(expectedUser)).thenReturn(expectedDto);

        UserDto actualDto = userService.getUserById(userId);

        assertEquals(expectedDto, actualDto);
        verify(userRepository).findById(userId);
        verify(userMapper).mapToUserDto(expectedUser);
    }

    @Test
    void getUserById_whenUserNotFound_thenThrownNotFoundException() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
        verify(userRepository).findById(userId);
        verify(userMapper, never()).mapToUserDto(any());
    }

    @Test
    void deleteUserById() {
        long userId = 1L;
        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUserById(userId);

        verify(userRepository).deleteById(userId);
    }
}