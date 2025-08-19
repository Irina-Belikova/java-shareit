package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        user = userRepository.createUser(user);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto updateUser(long id, UserDto userDto) {
        User updateUser = userRepository.getUserById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с таким id - %s не найден.", id)));

        if (userDto.hasName()) {
            updateUser.setName(userDto.getName());
        }

        if (userDto.hasEmail()) {
            updateUser.setEmail(userDto.getEmail());
        }

        updateUser = userRepository.updateUser(updateUser);
        return UserMapper.mapToUserDto(updateUser);
    }

    @Override
    public UserDto getUserById(long id) {
        User user = userRepository.getUserById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с таким id - %s не найден.", id)));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public void deleteUserById(long id) {
        userRepository.deleteUserById(id);
    }
}
