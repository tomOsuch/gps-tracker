package pl.tomaszosuch.gpstracker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GPS Tracker API")
                        .description("""
                                REST API do zarządzania urządzeniami mobilnymi
                                oraz zbierania i przechowywania danych lokalizacyjnych GPS.
                                
                                ## Możliwości
                                - Rejestracja i zarządzanie urządzeniami mobilnymi
                                - Wysyłanie i pobieranie danych GPS
                                - Obsługa bardzo dużej liczby równoczesnych urządzeń
                                """)
                        .version("1.0.0"))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Lokalny serwer developerski"),
                        new Server().url("http://app:8080").description("Serwer Docker")
                ));
    }
}
