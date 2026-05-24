package ru.url.shortcut.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication request")
public record AuthRequest(
        @Schema(
                description = "User login",
                example = "lfb346"
        )
        String login,

        @Schema(
                description = "User password",
                example = "password123"
        )
        String password
) {
}
