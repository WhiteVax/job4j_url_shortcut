package ru.url.shortcut.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.url.shortcut.dto.ConvertRequest;
import ru.url.shortcut.dto.ConvertResponse;
import ru.url.shortcut.dto.StatisticResponse;
import ru.url.shortcut.model.Site;
import ru.url.shortcut.service.SiteService;
import ru.url.shortcut.service.UrlService;

import java.security.Principal;
import java.util.List;

@RestController
public class UrlController {
    private final SiteService siteService;
    private final UrlService urlService;

    public UrlController(SiteService siteService, UrlService urlService) {
        this.siteService = siteService;
        this.urlService = urlService;
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/convert")
    public ResponseEntity<ConvertResponse> convert(@RequestBody ConvertRequest urlRequest,
                                                   Principal principal) {
        Site site = currentUser(principal);
        return ResponseEntity.ok(urlService.convert(urlRequest.url(), site));
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/statistic/{domain}")
    public List<StatisticResponse> statistic(@PathVariable String domain,
                                                             Principal principal) {
        return urlService.statisticByDomain(domain, principal);
    }

    @GetMapping("/redirect/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        return urlService.redirectUrl(code)
                .map(url -> ResponseEntity.status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, url.getOriginalUrl())
                        .<Void>build())
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private Site currentUser(Principal principal) {
        return siteService.findByLogin(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
}
