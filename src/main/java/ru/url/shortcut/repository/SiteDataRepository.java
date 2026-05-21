package ru.url.shortcut.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.url.shortcut.model.Site;

import java.util.List;
import java.util.Optional;

@Transactional
public interface SiteDataRepository extends CrudRepository<Site, Long> {
    Optional<Site> findByUrl(String url);

    Optional<Site> findByCode(String code);

    void deleteByUrl(String url);

    List<Site> findAllByUserLoginOrderByTotalDesc(String login);

    @Modifying
    @Query("UPDATE Site s SET s.total = s.total + 1  WHERE s.code = :code")
    int incrementTotal(String code);
}
