package br.com.fiap.tanormal.dto;

import br.com.fiap.tanormal.model.Trajeto;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalTime;
import java.util.List;

public final class TrajetoDto {

    private TrajetoDto() {
    }

    public record Request(
            @NotNull Long usuarioId,
            @NotBlank @Size(max = 60) @Schema(example = "Casa → FIAP") String nome,
            @NotNull Long origemId,
            @NotNull Long destinoId,
            @NotEmpty @Schema(description = "Linhas usadas, na ordem da viagem") List<@NotNull Long> linhaIds,
            @NotNull @JsonFormat(pattern = "HH:mm") @Schema(type = "string", example = "06:30") LocalTime horarioSaida,
            @NotNull @Positive @Max(300) @Schema(example = "75") Integer tempoNormalMinutos
    ) {
    }

    public record Response(
            Long id,
            Long usuarioId,
            String nome,
            LinhaDto.EstacaoResponse origem,
            LinhaDto.EstacaoResponse destino,
            List<String> linhas,
            @JsonFormat(pattern = "HH:mm") LocalTime horarioSaida,
            @JsonFormat(pattern = "HH:mm") LocalTime chegadaPrevista,
            Integer tempoNormalMinutos
    ) {
        public static Response de(Trajeto t) {
            return new Response(
                    t.getId(),
                    t.getUsuario().getId(),
                    t.getNome(),
                    LinhaDto.EstacaoResponse.de(t.getOrigem()),
                    LinhaDto.EstacaoResponse.de(t.getDestino()),
                    t.getLinhas().stream().map(l -> l.getNomeCompleto()).toList(),
                    t.getHorarioSaida(),
                    t.getHorarioChegadaPrevisto(),
                    t.getTempoNormalMinutos());
        }
    }
}
