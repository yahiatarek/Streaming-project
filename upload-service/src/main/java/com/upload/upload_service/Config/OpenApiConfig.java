package com.upload.upload_service.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI uploadServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Upload Service API")
                        .description("Manages uploaded video metadata and upload signatures.")
                        .version("v1")
                        .contact(new Contact().name("Streaming Project")));
    }
}
