package br.com.fiap.tanormal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class TaNormalApplication {

    public static void main(String[] args) {
        // O Render roda em UTC; os horários de saída e de relato são de São Paulo.
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
        SpringApplication.run(TaNormalApplication.class, args);
    }
}
