package ru.url.shortcut.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.url.shortcut.model.Site;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class SiteDataRepositoryTest {
    @Autowired
    private SiteDataRepository siteDataRepository;

    @AfterEach
    void clear() {
        siteDataRepository.deleteAll();
    }

    @Test
    void whenSaveSiteThenFindByUrlReturnsSite() {
        Site site = new Site();
        site.setUrl("https://job4j.ru");
        site.setCode("ABC123");
        site.setTotal(0);
        siteDataRepository.save(site);
        var result = siteDataRepository.findByUrl("https://job4j.ru");
        assertThat(result).isNotNull();
        assertThat(result.get().getCode()).isEqualTo("ABC123");
    }

    @Test
    void whenDeleteByUrlThenSiteRemoved() {
        Site site = new Site();
        site.setUrl("https://delete.com");
        site.setCode("DEL123");
        site.setTotal(0);
        siteDataRepository.save(site);
        siteDataRepository.deleteByUrl("https://delete.com");
        var result = siteDataRepository.findByUrl("https://delete.com");
        assertThat(result).isEmpty();
    }

    @Test
    void whenMultiIncrementTotalThenValueIncreased() {
        Site site = new Site();
        site.setUrl("https://test.com");
        site.setCode("CODE1");
        site.setTotal(0);
        siteDataRepository.save(site);
        siteDataRepository.incrementTotal("CODE1");
        siteDataRepository.incrementTotal("CODE1");
        siteDataRepository.incrementTotal("CODE1");
        var updated = siteDataRepository.findByUrl("https://test.com");
        assertThat(updated.get().getTotal()).isEqualTo(3);
    }
}
