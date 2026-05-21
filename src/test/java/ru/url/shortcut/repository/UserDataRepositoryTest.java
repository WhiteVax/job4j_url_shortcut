package ru.url.shortcut.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.url.shortcut.model.Role;
import ru.url.shortcut.model.User;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class UserDataRepositoryTest {

    @Autowired
    private UserDataRepository userDataRepository;

    @AfterEach
    void clear() {
        userDataRepository.deleteAll();
    }

    @Test
    void whenSaveUserThenFindByLoginReturnsUser() {
        User user = new User();
        user.setLogin("testUser");
        user.setPassword("123456");
        user.setRole(Role.USER);
        userDataRepository.save(user);
        Optional<User> result = userDataRepository.findByLogin("testUser");
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getLogin()).isEqualTo("testUser");
    }

    @Test
    void whenFindByLoginNotExistsThenReturnEmptyList() {
        Optional<User> result = userDataRepository.findByLogin("noUser");
        assertThat(result).isEmpty();
    }

    @Test
    void whenDeleteByLoginAndPasswordThenUserRemoved() {
        User user = new User();
        user.setLogin("deleteUser");
        user.setPassword("pass123");
        user.setRole(Role.USER);
        userDataRepository.save(user);
        userDataRepository.deleteByLoginAndPassword("deleteUser", "pass123");
        Optional<User> result = userDataRepository.findByLogin("deleteUser");
        assertThat(result).isEmpty();
    }
}
