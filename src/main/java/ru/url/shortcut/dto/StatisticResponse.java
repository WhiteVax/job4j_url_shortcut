package ru.url.shortcut.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "URL statistics response")
public record StatisticResponse(

        @Schema(
                description = "Original URL",
                example = "https://google.com"
        )
        String url,

        @Schema(
                description = "Number of redirects",
                example = "15"
        )
        int total
) {
}
