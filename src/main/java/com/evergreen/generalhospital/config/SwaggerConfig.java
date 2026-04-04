package com.evergreen.generalhospital.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI hospitalOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Evergreen General Hospital API")
                .description("REST API for managing patients, practitioners, appointments, departments, and user accounts")
                .version("v1.0.0")
                .contact(new Contact()
                    .name("Evergreen General Hospital Engineering")
                    .email("engineering@evergreengeneralhospital.com")))
            .components(new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .name("bearerAuth")
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")));
    }
}
