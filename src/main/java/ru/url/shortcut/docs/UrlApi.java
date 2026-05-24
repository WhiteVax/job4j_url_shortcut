package ru.url.shortcut.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.url.shortcut.dto.ConvertRequest;
import ru.url.shortcut.dto.ConvertResponse;
import ru.url.shortcut.dto.StatisticResponse;

import java.security.Principal;
import java.util.List;

public interface UrlApi {

    @Operation(summary = "Convert URL to short code")
    @ApiResponse(responseCode = "200", description = "URL successfully converted")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<ConvertResponse> convert(
            @RequestBody ConvertRequest urlRequest,
            Principal principal
    );

    @Operation(summary = "Get statistics for current site")
    @ApiResponse(responseCode = "200", description = "Statistics successfully retrieved")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @SecurityRequirement(name = "bearerAuth")
    List<StatisticResponse> statistic(
            @PathVariable String domain,
            Principal principal
    );

    @Operation(summary = "Redirect by short code")
    @ApiResponse(responseCode = "302", description = "Redirect successful")
    @ApiResponse(responseCode = "404", description = "Code not found")
    ResponseEntity<Void> redirect(
            @PathVariable String code
    );
}
