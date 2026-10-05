package com.example.seurity.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI zanzimartOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Zanzimart System API")
                .version("v1")
                .description("Security configuration.")
                .contact(new Contact().name("ZANZIMART Technologies Ltd")));
    }
}