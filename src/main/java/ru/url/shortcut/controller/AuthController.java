package ru.url.shortcut.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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
import ru.url.shortcut.service.UserService;

@RestController
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager,
                          UserDetailsService userDetailsService,
                          JwtService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Register site and issue login/password pair")
    @PostMapping("/registration")
    public ResponseEntity<RegistrationResponse> registration(
            @RequestBody RegistrationRequest request) {
        RegistrationResponse response = userService.register(request.site());
        HttpStatus status = response.registration() ? HttpStatus.CREATED : HttpStatus.OK;
        return new ResponseEntity<>(response, status);
    }

    @Operation(summary = "Authenticate site account and issue JWT")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.login(), request.password()));
        var userDetails = userDetailsService.loadUserByUsername(request.login());
        return ResponseEntity.ok(new AuthResponse(jwtService.generateToken(userDetails)));
    }
}
