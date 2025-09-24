package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserRepositoryTest {

    private final UserRepository userRepository;

    private User user1;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        user1 = User.builder()
                .name("name1")
                .email("name1@mail.com")
                .build();
    }

    @Test
    void saveTest() {
        User savedUser = userRepository.save(user1);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("name1");
        assertThat(savedUser.getEmail()).isEqualTo("name1@mail.com");

        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    void findById_whenUserFound_thenReturnUser() {
        User savedUser = userRepository.save(user1);

        Optional<User> foundUser = userRepository.findById(user1.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    void findById_whenUserNotFound_thenReturnEmpty() {
        Optional<User> foundUser = userRepository.findById(1L);

        assertThat(foundUser).isEmpty();
    }

    @Test
    void deleteByIdTest() {
        User savedUser = userRepository.save(user1);
        assertThat(userRepository.count()).isEqualTo(1);

        userRepository.deleteById(savedUser.getId());

        assertThat(userRepository.count()).isEqualTo(0);
        assertThat(userRepository.findById(savedUser.getId())).isEmpty();
    }

    @Test
    void findByEmail_whenEmailFound_thenReturnUser() {
        User savedUser = userRepository.save(user1);

        Optional<User> foundUser = userRepository.findByEmail("name1@mail.com");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(savedUser.getId());
        assertThat(foundUser.get().getName()).isEqualTo("name1");
        assertThat(foundUser.get().getEmail()).isEqualTo("name1@mail.com");
    }

    @Test
    void findByEmail_whenEmailNotFound_thenReturnEmpty() {
        userRepository.save(user1);

        Optional<User> foundUser = userRepository.findByEmail("pochta@mail.com");

        assertThat(foundUser).isEmpty();
    }

    @Test
    void existsById_whenIdFound_thenReturnTrue() {
        User savedUser = userRepository.save(user1);

        boolean isExists = userRepository.existsById(savedUser.getId());

        assertTrue(isExists);
    }

    @Test
    void existsById_whenIdNotFound_thenReturnFalse() {
        boolean isExists = userRepository.existsById(1L);

        assertFalse(isExists);
    }
}