package ru.url.shortcut.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.url.shortcut.dto.AuthRequest;
import ru.url.shortcut.dto.AuthResponse;
import ru.url.shortcut.dto.RegistrationRequest;
import ru.url.shortcut.dto.RegistrationResponse;
import ru.url.shortcut.security.JwtService;
import ru.url.shortcut.service.SiteService;

@RestController
public class AuthController {
    private final SiteService siteService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthController(SiteService siteService,
                          AuthenticationManager authenticationManager,
                          UserDetailsService userDetailsService,
                          JwtService jwtService) {
        this.siteService = siteService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @PostMapping("/registration")
    public ResponseEntity<RegistrationResponse> registration(
            @RequestBody RegistrationRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(siteService.register(request.site()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.login(),
                            request.password()
                    )
            );
            var userDetails = userDetailsService.loadUserByUsername(request.login());
            return ResponseEntity.ok(
                    new AuthResponse(jwtService.generateToken(userDetails))
            );
        } catch (BadCredentialsException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(String.format("Invalid username or password, json body - %s", request));
        }
    }
}
