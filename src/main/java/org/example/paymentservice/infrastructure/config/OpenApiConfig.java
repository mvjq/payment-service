package org.example.paymentservice.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI paymentServiceOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Development Server");

        Info info = new Info()
                .title("Payment Service API")
                .version("1.0.0")
                .description("REST API for managing payments and webhooks");

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}
