package com.example.demo.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI hospitalOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Hospital Management API")
                .description("REST API for managing patients, doctors, appointments and medical records")
                .version("v1.0.0")
                .contact(new Contact()
                    .name("Tu Nombre")
                    .email("tu@email.com")));
            // .externalDocs(new ExternalDocumentation()
            //     .description("Documentation")
            //     .url("https://tuportfolio.com"));
    }
}
