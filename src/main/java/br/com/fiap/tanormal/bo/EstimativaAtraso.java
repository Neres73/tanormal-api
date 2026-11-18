package br.com.fiap.tanormal.bo;

import br.com.fiap.tanormal.model.StatusLinha;

import java.util.List;

/** Resultado do {@link EstimadorAtraso}. */
public record EstimativaAtraso(
        int atrasoMinutos,
        int chanceLentidaoPercentual,
        boolean rotaAlternativaRecomendada,
        List<ImpactoLinha> impactos
) {
    /**
     * Quanto cada linha do trajeto contribui para o atraso.
     * {@code divergencia} = o site diz "operação normal", mas os passageiros relatam lentidão.
     */
    public record ImpactoLinha(
            Long linhaId,
            String nomeLinha,
            StatusLinha statusOficial,
            int minutosPeloStatus,
            int minutosPelosRelatos,
            int quantidadeRelatos,
            int atrasoMinutos,
            boolean divergencia
    ) {
    }
}
