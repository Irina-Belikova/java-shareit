package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        user = userRepository.save(user);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(long id, UserDto userDto) {
        User updateUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с таким id - %s не найден.", id)));

        if (userDto.hasName()) {
            updateUser.setName(userDto.getName());
        }

        if (userDto.hasEmail()) {
            updateUser.setEmail(userDto.getEmail());
        }

        updateUser = userRepository.save(updateUser);
        return UserMapper.mapToUserDto(updateUser);
    }

    @Override
    public UserDto getUserById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с таким id - %s не найден.", id)));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public void deleteUserById(long id) {
        userRepository.deleteById(id);
    }
}
