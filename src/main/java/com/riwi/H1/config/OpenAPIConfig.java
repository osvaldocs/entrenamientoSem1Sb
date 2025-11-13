package com.riwi.H1.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Catálogo de Eventos y Venues - Riwi")
                        .version("1.0.0")
                        .description("API REST In-Memory para gestionar eventos y venues"));
    }
}
