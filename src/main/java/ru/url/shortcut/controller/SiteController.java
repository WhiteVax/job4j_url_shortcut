package ru.url.shortcut.controller;

import io.swagger.v3.oas.annotations.Operation;
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
import ru.url.shortcut.model.User;
import ru.url.shortcut.service.SiteService;
import ru.url.shortcut.service.UserService;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
public class SiteController {
    private final SiteService siteService;
    private final UserService userService;

    public SiteController(SiteService siteService, UserService userService) {
        this.siteService = siteService;
        this.userService = userService;
    }

    @Operation(summary = "Convert URL to short code")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/convert")
    public ResponseEntity<ConvertResponse> convert(@RequestBody ConvertRequest request,
                                                   Principal principal) {
        User user = currentUser(principal);
        return ResponseEntity.ok(siteService.convert(request.url(), user));
    }

    @Operation(summary = "Redirect by short code")
    @GetMapping("/redirect/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        return siteService.redirectUrl(code)
                .map(url -> ResponseEntity.status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, URI.create(url).toString())
                        .<Void>build())
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get current site URL statistics")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/statistic")
    public ResponseEntity<List<StatisticResponse>> statistic(Principal principal) {
        return ResponseEntity.ok(siteService.findStatistic(principal.getName()));
    }

    private User currentUser(Principal principal) {
        return userService.findByLogin(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
}
