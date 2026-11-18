package br.com.fiap.tanormal.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

/** Formato único de erro devolvido pela API. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErroResponse(
        LocalDateTime dataHora,
        int status,
        String erro,
        String mensagem,
        String caminho,
        List<CampoInvalido> campos
) {
    public record CampoInvalido(String campo, String mensagem) {
    }
}
