package ru.url.shortcut.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Site registration response")
public record RegistrationResponse(

        @Schema(
                description = "Registered site domain",
                example = "google.com"
        )
        String site,

        @Schema(
                description = "Generated login",
                example = "lfb346"
        )
        String login,

        @Schema(
                description = "Generated password",
                example = "pass123"
        )
        String password
) {
}
