package br.com.fiap.tanormal.dto;

import br.com.fiap.tanormal.model.Estacao;
import br.com.fiap.tanormal.model.Linha;
import br.com.fiap.tanormal.model.Operadora;
import br.com.fiap.tanormal.model.StatusLinha;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class LinhaDto {

    private LinhaDto() {
    }

    public record Request(
            @NotNull @Positive Integer numero,
            @NotBlank @Size(max = 40) String nome,
            @NotNull Operadora operadora
    ) {
    }

    /** Usado pelo leitor do site do Metrô/CPTM para atualizar o status oficial. */
    public record StatusRequest(@NotNull StatusLinha status) {
    }

    public record Response(
            Long id,
            Integer numero,
            String nome,
            String nomeCompleto,
            Operadora operadora,
            StatusLinha statusOficial,
            String statusDescricao,
            LocalDateTime dataAtualizacaoStatus
    ) {
        public static Response de(Linha l) {
            return new Response(l.getId(), l.getNumero(), l.getNome(), l.getNomeCompleto(),
                    l.getOperadora(), l.getStatusOficial(), l.getStatusOficial().getDescricao(),
                    l.getDataAtualizacaoStatus());
        }
    }

    public record EstacaoRequest(
            @NotBlank @Size(max = 80) String nome,
            @NotNull Long linhaId
    ) {
    }

    public record EstacaoResponse(Long id, String nome, Long linhaId, String linha) {

        public static EstacaoResponse de(Estacao e) {
            return new EstacaoResponse(e.getId(), e.getNome(), e.getLinha().getId(),
                    e.getLinha().getNomeCompleto());
        }
    }
}
