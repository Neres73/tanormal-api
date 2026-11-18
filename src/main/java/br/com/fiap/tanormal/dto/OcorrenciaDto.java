package br.com.fiap.tanormal.dto;

import br.com.fiap.tanormal.model.FonteOcorrencia;
import br.com.fiap.tanormal.model.Ocorrencia;
import br.com.fiap.tanormal.model.StatusLinha;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class OcorrenciaDto {

    private OcorrenciaDto() {
    }

    public record Request(
            @NotNull Long linhaId,
            @NotNull StatusLinha status,
            @NotNull FonteOcorrencia fonte,
            @Size(max = 300) String descricao,
            Boolean chuva,
            @PastOrPresent LocalDateTime inicio
    ) {
    }

    /** Corpo opcional do encerramento; sem ele, encerra agora. */
    public record EncerramentoRequest(@PastOrPresent LocalDateTime fim) {
    }

    public record Response(
            Long id,
            Long linhaId,
            String linha,
            StatusLinha status,
            FonteOcorrencia fonte,
            String descricao,
            Boolean chuva,
            LocalDateTime inicio,
            LocalDateTime fim,
            boolean aberta,
            long duracaoMinutos
    ) {
        public static Response de(Ocorrencia o) {
            return new Response(
                    o.getId(),
                    o.getLinha().getId(),
                    o.getLinha().getNomeCompleto(),
                    o.getStatus(),
                    o.getFonte(),
                    o.getDescricao(),
                    o.getChuva(),
                    o.getInicio(),
                    o.getFim(),
                    o.isAberta(),
                    o.getDuracaoMinutos());
        }
    }
}
