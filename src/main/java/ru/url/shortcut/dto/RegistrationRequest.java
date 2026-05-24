package ru.url.shortcut.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Site registration request")
public record RegistrationRequest(

        @Schema(
                description = "Site domain name",
                example = "google.com"
        )
        String site
) {
}
