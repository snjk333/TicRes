package com.oleksandr.registerms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TicRes RegisterMS API")
                        .version("1.0.0")
                        .description("TicRes Authentication & User Management Service (Reactive WebFlux)")
                        .contact(new Contact()
                                .name("Oleksandr")
                                .email("kulbitsanya0@gmail.com")));
    }
}
