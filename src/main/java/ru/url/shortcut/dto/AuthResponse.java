package ru.url.shortcut.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT authentication response")
public record AuthResponse(

        @Schema(
                description = "JWT access token",
                example = "eyJhbGciOiJIUzI1NiJ9..."
        )
        String token
) {
}
