package ru.url.shortcut.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import ru.url.shortcut.dto.AuthRequest;
import ru.url.shortcut.dto.RegistrationRequest;
import ru.url.shortcut.dto.RegistrationResponse;

public interface AuthApi {

    @Operation(summary = "Register site and issue login/password pair")
    @ApiResponse(responseCode = "201", description = "Site successfully registered")
    ResponseEntity<RegistrationResponse> registration(
            @RequestBody RegistrationRequest request
    );

    @Operation(summary = "Authenticate site account and issue JWT")
    @ApiResponse(responseCode = "200", description = "Authentication successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    ResponseEntity<?> login(
            @RequestBody AuthRequest request
    );
}
