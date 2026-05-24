package ru.url.shortcut.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.url.shortcut.model.Site;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface SiteDataRepository extends CrudRepository<Site, Integer> {
    Optional<Site> findByLogin(String code);
}
