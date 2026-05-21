package ru.url.shortcut.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.url.shortcut.model.User;

import java.util.Optional;

@Transactional
public interface UserDataRepository extends CrudRepository<User, Long> {
    Optional<User> findByLogin(String name);

    @Modifying
    @Query("DELETE FROM User u WHERE u.login = :login AND u.password = :password")
    void deleteByLoginAndPassword(@Param("login") String name,
                                  @Param("password") String password);
}
