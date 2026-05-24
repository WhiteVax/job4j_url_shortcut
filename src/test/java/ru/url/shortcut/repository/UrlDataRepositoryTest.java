package ru.url.shortcut.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.url.shortcut.model.Role;
import ru.url.shortcut.model.Site;
import ru.url.shortcut.model.Url;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class UrlDataRepositoryTest {
    @Autowired
    private UrlDataRepository urlDataRepository;
    @Autowired
    private SiteDataRepository siteDataRepository;

    @AfterEach
    void clear() {
        urlDataRepository.deleteAll();
        siteDataRepository.deleteAll();
    }

    @Test
    void whenSaveUrlAndFinByCodeThenSuccess() {
        Site site = new Site();
        site.setDomain("job4j.ru");
        site.setLogin("lfb346");
        site.setPassword("lfb346");
        site.setRole(Role.USER);

        Url url = new Url();
        url.setOriginalUrl("job4j.ru/123sdfvbd");
        url.setSite(site);
        url.setCode("3cvbsdf");
        siteDataRepository.save(site);
        urlDataRepository.save(url);
        var result = urlDataRepository.findByCode("3cvbsdf");
        assertThat(result).isNotNull();
        assertThat(result.get().getSite().getDomain()).isEqualTo("job4j.ru");
        assertThat(result.get().getOriginalUrl()).isEqualTo("job4j.ru/123sdfvbd");
    }

    @Test
    void whenSaveUrlsWithSitesMultiThenFindTheseUrls() {
        Site site = new Site();
        site.setDomain("job4j.ru");
        site.setLogin("lfb346");
        site.setPassword("lfb346");
        site.setRole(Role.USER);

        siteDataRepository.save(site);
        Url first = new Url();
        first.setOriginalUrl("job4j.ru/123sd/fvbd");
        first.setCode("3cvbsdf");
        first.setSite(site);

        Url second = new Url();
        second.setOriginalUrl("job4j.ru/123sdfvbd");
        second.setCode("3c23v23b2sdf");
        second.setSite(site);

        urlDataRepository.save(first);
        urlDataRepository.save(second);

        List<Url> result =
                urlDataRepository.findAllBySiteDomainOrderByTotalDesc(
                        site.getDomain(), site.getLogin());
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getOriginalUrl()).isEqualTo(first.getOriginalUrl());
        assertThat(result.get(1).getOriginalUrl()).isEqualTo(second.getOriginalUrl());
    }

    @Test
    void whenMultiIncrementTotalThenFindTheseUrlCheckingTotal() {
       Site site = new Site();
       site.setDomain("job4j.ru");
       site.setLogin("lfb346");
       site.setPassword("lfb346");
       site.setRole(Role.USER);
       siteDataRepository.save(site);

        Url url = new Url();
        url.setOriginalUrl("job4j.ru/123sdfvbd");
        url.setSite(site);
        url.setCode("3cvbsdf");

        urlDataRepository.save(url);
        urlDataRepository.incrementTotal("3cvbsdf");
        urlDataRepository.incrementTotal("3cvbsdf");
        urlDataRepository.incrementTotal("3cvbsdf");
        var updated = urlDataRepository.findByCode("3cvbsdf");
        assertThat(updated.get().getTotal()).isEqualTo(3);
        assertThat(updated.get().getCode()).isEqualTo("3cvbsdf");
    }
}
