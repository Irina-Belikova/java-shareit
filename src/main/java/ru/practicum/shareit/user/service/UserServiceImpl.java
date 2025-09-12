package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.mapToUser(userDto);
        user = userRepository.save(user);
        return userMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(long id, UserDto userDto) {
        User updateUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с таким id - %s не найден.", id)));
        userMapper.updateUserFromDto(userDto, updateUser);
        return userMapper.mapToUserDto(updateUser);
    }

    @Override
    public UserDto getUserById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с таким id - %s не найден.", id)));
        return userMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public void deleteUserById(long id) {
        userRepository.deleteById(id);
    }
}
