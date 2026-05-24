package ru.url.shortcut.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "URL conversion request")
public record ConvertRequest(

        @Schema(
                description = "Original URL to shorten",
                example = "https://google.com"
        )
        String url
) {
}
