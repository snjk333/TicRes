package com.oleksandr.eventprovider.configuration.openAPI;

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
                        .title("TicRes EventProvider API")
                        .version("1.0.0")
                        .description("TicRes External Events & Tickets Provider - Ticketmaster Integration")
                        .contact(new Contact()
                                .name("Oleksandr")
                                .email("kulbitsanya0@gmail.com")));
    }
}
