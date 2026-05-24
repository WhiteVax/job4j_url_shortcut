package ru.url.shortcut.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.url.shortcut.model.Url;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlDataRepository extends CrudRepository<Url, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE Url u SET u.total = u.total + 1  WHERE u.code = :code")
    int incrementTotal(@Param("code") String code);

    @Query("""
            SELECT u
            FROM Url u
            WHERE u.site.domain = :domain AND u.site.login = :login
            ORDER BY u.total DESC
            """)
    List<Url> findAllBySiteDomainOrderByTotalDesc(@Param("domain") String domain,
                                                  @Param("login") String login);

    @Query("""
            SELECT u
            FROM Url u
            WHERE u.code = :code
            """)
    Optional<Url> findByCode(@Param("code") String code);
}
