package com.coindcx.springclient.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for CoinDCX Spring Client REST APIs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI coinDCXOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CoinDCX Spring Client API")
                        .description("REST API documentation for CoinDCX cryptocurrency trading platform integration. " +
                                "This API provides comprehensive endpoints for spot trading, futures trading, " +
                                "margin trading, lending, wallet management, and market data.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("CoinDCX API Support")
                                .url("https://coindcx.com")
                                .email("support@coindcx.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.coindcx.com")
                                .description("Production Server (CoinDCX)")))
                .components(new Components()
                        .addSecuritySchemes("apiKey", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-API-KEY")
                                .description("CoinDCX API Key for authentication"))
                        .addSecuritySchemes("apiSecret", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-API-SECRET")
                                .description("CoinDCX API Secret for request signing")))
                .addSecurityItem(new SecurityRequirement()
                        .addList("apiKey")
                        .addList("apiSecret"));
    }
}
