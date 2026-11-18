package br.com.fiap.tanormal.dto;

import br.com.fiap.tanormal.model.Relato;
import br.com.fiap.tanormal.model.TipoRelato;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class RelatoDto {

    private RelatoDto() {
    }

    public record Request(
            @NotNull Long usuarioId,
            @NotNull Long linhaId,
            Long estacaoId,
            @NotNull TipoRelato tipo,
            @Size(max = 200) String comentario
    ) {
    }

    public record Response(
            Long id,
            Long usuarioId,
            Long linhaId,
            String linha,
            Long estacaoId,
            String estacao,
            TipoRelato tipo,
            String comentario,
            LocalDateTime dataHora,
            Integer confirmacoes,
            boolean valido
    ) {
        public static Response de(Relato r) {
            return new Response(
                    r.getId(),
                    r.getUsuario().getId(),
                    r.getLinha().getId(),
                    r.getLinha().getNomeCompleto(),
                    r.getEstacao() != null ? r.getEstacao().getId() : null,
                    r.getEstacao() != null ? r.getEstacao().getNome() : null,
                    r.getTipo(),
                    r.getComentario(),
                    r.getDataHora(),
                    r.getConfirmacoes(),
                    r.isValido(LocalDateTime.now()));
        }
    }
}
