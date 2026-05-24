package ru.url.shortcut.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.url.shortcut.dto.ConvertResponse;
import ru.url.shortcut.dto.StatisticResponse;
import ru.url.shortcut.model.Site;
import ru.url.shortcut.model.Url;
import ru.url.shortcut.repository.SiteDataRepository;
import ru.url.shortcut.repository.UrlDataRepository;
import ru.url.shortcut.utils.ConvertorForSite;

import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class UrlService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlService.class);
    private final SiteDataRepository siteDataRepository;
    private UrlDataRepository urlDataRepository;
    private ConvertorForSite convertorForSite;
    private SiteService siteService;

    public UrlService(UrlDataRepository urlDataRepository, ConvertorForSite convertorForSite, SiteService siteService, SiteDataRepository siteDataRepository) {
        this.urlDataRepository = urlDataRepository;
        this.convertorForSite = convertorForSite;
        this.siteService = siteService;
        this.siteDataRepository = siteDataRepository;
    }

    public ConvertResponse convert(String urlReq, Site site) {
        Url url = new Url();
        url.setSite(site);
        url.setOriginalUrl(urlReq);
        try {
            String code = convertorForSite.urlConvertor(urlReq);
            url.setCode(code);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e.getMessage());
        }
        urlDataRepository.save(url);
        return new ConvertResponse(url.getCode());
    }

    @Transactional
    public Optional<Url> redirectUrl(String code) {
        Optional<Url> url = urlDataRepository.findByCode(code);
        url.ifPresent(value -> urlDataRepository.incrementTotal(code));
        return url;
    }

    public List<StatisticResponse> statisticByDomain(String domain, Principal principal) {
        var siteDB = siteService.findByLogin(principal.getName());
        if (!domain.equals(siteDB.get().getDomain())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Access denied for domain: " + domain
            );
        }
        return urlDataRepository
                .findAllBySiteDomainOrderByTotalDesc(domain, principal.getName())
                .stream()
                .map(u -> new StatisticResponse(u.getOriginalUrl(), u.getTotal()))
                .toList();
    }

    public void deleteAll() {
        urlDataRepository.deleteAll();
    }
}
