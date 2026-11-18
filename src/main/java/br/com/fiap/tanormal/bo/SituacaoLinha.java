package br.com.fiap.tanormal.bo;

import br.com.fiap.tanormal.model.StatusLinha;
import br.com.fiap.tanormal.model.TipoRelato;

import java.util.List;

/**
 * Foto do momento de uma linha: o status oficial e os relatos ainda válidos.
 * É a entrada do {@link EstimadorAtraso}.
 */
public record SituacaoLinha(
        Long linhaId,
        String nomeLinha,
        StatusLinha statusOficial,
        List<RelatoAtivo> relatos
) {
    public record RelatoAtivo(TipoRelato tipo, int confirmacoes) {
    }
}
