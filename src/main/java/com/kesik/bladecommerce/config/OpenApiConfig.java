package com.kesik.bladecommerce.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.servers.Server;


@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addServersItem(
                        new Server()
                                .url("https://kesik-bicakcilik.up.railway.app")
                                .description("Production")
                );
    }
}
