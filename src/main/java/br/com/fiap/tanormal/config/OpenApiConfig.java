package br.com.fiap.tanormal.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI tanormalOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Tá Normal? API")
                .version("1.0.0")
                .description("Avisa antes de você sair de casa quanto a sua viagem de metrô/CPTM vai atrasar. "
                        + "Global Solution 2026.2 - CitizenTech - FIAP"));
    }
}
