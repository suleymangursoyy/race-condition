package com.sgursoy.racecondition.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

        @Value("${server.port:8080}")
        private String serverPort;

        @Bean
        public OpenAPI customOpenAPI() {
                Server server = new Server();
                server.setUrl("http://localhost:" + serverPort);
                server.setDescription("Development Server");

                Contact contact = new Contact();
                contact.setName("Süleyman Gürsoy");
                contact.setEmail("your-email@example.com");

                License license = new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT");

                Info info = new Info()
                                .title("Race Condition Ticketing API")
                                .version("1.0.0")
                                .description("API for managing event ticketing with race condition handling")
                                .contact(contact)
                                .license(license);

                return new OpenAPI()
                                .info(info)
                                .servers(List.of(server));
        }
}
