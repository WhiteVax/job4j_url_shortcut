package ru.url.shortcut.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.url.shortcut.dto.ConvertResponse;
import ru.url.shortcut.dto.StatisticResponse;
import ru.url.shortcut.model.Site;
import ru.url.shortcut.model.User;
import ru.url.shortcut.repository.SiteDataRepository;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Service
public class SiteService {
    private final SiteDataRepository siteData;
    private final ConvertorService convertorService;

    public SiteService(SiteDataRepository siteDataRepository,
                       ConvertorService convertorService) {
        this.siteData = siteDataRepository;
        this.convertorService = convertorService;
    }

    public Site createSite(String url, User user) {
        Site site = new Site();
        site.setUrl(url);
        site.setCode(uniqueCode(url));
        site.setTotal(0);
        site.setUser(user);
        return siteData.save(site);
    }

    public ConvertResponse convert(String url, User user) {
        return siteData.findByUrl(url)
                .map(site -> new ConvertResponse(site.getCode()))
                .orElseGet(() -> new ConvertResponse(createSite(url, user).getCode()));
    }

    public void delete(String url) {
        siteData.deleteByUrl(url);
    }

    public List<StatisticResponse> findStatistic(String login) {
        return siteData.findAllByUserLoginOrderByTotalDesc(login).stream()
                .map(site -> new StatisticResponse(site.getUrl(), site.getTotal()))
                .toList();
    }

    @Transactional
    public Optional<String> redirectUrl(String code) {
        int updated = siteData.incrementTotal(code);
        if (updated == 0) {
            return Optional.empty();
        }
        return siteData.findByCode(code).map(Site::getUrl);
    }

    public Optional<Site> siteFindByUrl(String url) {
        return siteData.findByUrl(url);
    }

    private String uniqueCode(String url) {
        String code = convertUrl(url);
        int suffix = 1;
        while (siteData.findByCode(code).isPresent()) {
            code = convertUrl(url + suffix);
            suffix++;
        }
        return code;
    }

    private String convertUrl(String url) {
        try {
            return convertorService.urlConvertor(url);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Can not create short code", e);
        }
    }
}
