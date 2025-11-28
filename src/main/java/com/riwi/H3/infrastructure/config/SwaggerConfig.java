package com.riwi.H3.infrastructure.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Catálogo HU3 - Eventos y Venues")
                        .version("1.0")
                        .description("API REST para gestión de eventos y venues con arquitectura hexagonal"));
    }
}
