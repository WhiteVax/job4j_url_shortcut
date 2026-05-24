package ru.url.shortcut.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Shortened URL response")
public record ConvertResponse(

        @Schema(
                description = "Generated short URL code",
                example = "A1b2C3"
        )
        String code
) {
}
