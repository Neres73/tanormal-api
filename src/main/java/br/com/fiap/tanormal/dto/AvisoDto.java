package br.com.fiap.tanormal.dto;

import br.com.fiap.tanormal.bo.EstimativaAtraso;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/** O aviso que chega no celular antes do horário de saída. */
public record AvisoDto(
        Long trajetoId,
        String trajeto,
        String mensagem,
        int atrasoEstimadoMinutos,
        int chanceLentidaoPercentual,
        @JsonFormat(pattern = "HH:mm") LocalTime horarioSaidaHabitual,
        @JsonFormat(pattern = "HH:mm") LocalTime horarioSaidaSugerido,
        @JsonFormat(pattern = "HH:mm") LocalTime chegadaPrevista,
        boolean rotaAlternativaRecomendada,
        boolean chuvaConsiderada,
        List<EstimativaAtraso.ImpactoLinha> linhas,
        LocalDateTime geradoEm
) {
}
