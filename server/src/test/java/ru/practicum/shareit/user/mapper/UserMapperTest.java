package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @InjectMocks
    private UserMapperImpl userMapper;

    @Test
    void mapToUserDtoTest() {
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("pochta@mail.com")
                .build();

        UserDto userDto = userMapper.mapToUserDto(user);

        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getName()).isEqualTo("name");
        assertThat(userDto.getEmail()).isEqualTo("pochta@mail.com");
    }

    @Test
    void mapToUser_shouldIgnoreId() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Kot")
                .email("kot@mail.com")
                .build();

        User user = userMapper.mapToUser(userDto);

        assertThat(user.getId()).isNull();
        assertThat(user.getName()).isEqualTo("Kot");
        assertThat(user.getEmail()).isEqualTo("kot@mail.com");
    }

    @Test
    void updateUserFromDto_withValidFields() {
        User existUser = User.builder()
                .id(1L)
                .name("name")
                .email("pochta@mail.com")
                .build();
        UserDto updateDto = UserDto.builder()
                .id(1L)
                .name("Kot")
                .email("kot@mail.com")
                .build();

        userMapper.updateUserFromDto(updateDto, existUser);

        assertThat(existUser.getId()).isEqualTo(1L);
        assertThat(existUser.getName()).isEqualTo("Kot");
        assertThat(existUser.getEmail()).isEqualTo("kot@mail.com");
    }

    @Test
    void updateUserFromDto_withFailFields() {
        User existUser = User.builder()
                .id(1L)
                .name("name")
                .email("pochta@mail.com")
                .build();
        UserDto updateDto = UserDto.builder()
                .id(9L)
                .name(null)
                .email(" ")
                .build();

        userMapper.updateUserFromDto(updateDto, existUser);

        assertThat(existUser.getId()).isEqualTo(1L);
        assertThat(existUser.getName()).isEqualTo("name");
        assertThat(existUser.getEmail()).isEqualTo("pochta@mail.com");
    }
}