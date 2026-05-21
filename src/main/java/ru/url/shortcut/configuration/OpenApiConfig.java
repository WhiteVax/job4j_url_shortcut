package ru.url.shortcut.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${url-shortcut.openapi.dev-url}")
    private String devUrl;

    @Value("${url-shortcut.openapi.prod-url}")
    private String prodUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        Server serverDev = new Server()
                .url(devUrl)
                .description("Development API");

        Server serverProd = new Server()
                .url(prodUrl)
                .description("Production API");

        License license = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("url-shortcut API")
                .version("1.0")
                .license(license);
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
        return new OpenAPI()
                .info(info)
                .servers(List.of(serverProd, serverDev))
                .components(new Components().addSecuritySchemes("bearerAuth", bearerAuth));
    }
}
