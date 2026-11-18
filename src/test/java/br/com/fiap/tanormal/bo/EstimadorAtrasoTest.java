package br.com.fiap.tanormal.bo;

import br.com.fiap.tanormal.model.StatusLinha;
import br.com.fiap.tanormal.model.TipoRelato;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EstimadorAtrasoTest {

    private final EstimadorAtraso estimador = new EstimadorAtraso();

    @Test
    void semProblemaNenhumNaoTemAtraso() {
        EstimativaAtraso r = estimador.estimar(List.of(
                linha(StatusLinha.NORMAL),
                linha(StatusLinha.NORMAL)), false);

        assertEquals(0, r.atrasoMinutos());
        assertFalse(r.rotaAlternativaRecomendada());
    }

    @Test
    void velocidadeReduzidaComChuvaAumentaOAtraso() {
        EstimativaAtraso semChuva = estimador.estimar(List.of(linha(StatusLinha.VELOCIDADE_REDUZIDA)), false);
        EstimativaAtraso comChuva = estimador.estimar(List.of(linha(StatusLinha.VELOCIDADE_REDUZIDA)), true);

        assertEquals(10, semChuva.atrasoMinutos());
        assertEquals(12, comChuva.atrasoMinutos());
        assertTrue(comChuva.chanceLentidaoPercentual() > semChuva.chanceLentidaoPercentual());
    }

    @Test
    void statusNormalComPassageirosRelatandoLentidaoEhDivergencia() {
        SituacaoLinha linha = new SituacaoLinha(1L, "Linha 12-Safira", StatusLinha.NORMAL, List.of(
                new SituacaoLinha.RelatoAtivo(TipoRelato.PARADO, 3),
                new SituacaoLinha.RelatoAtivo(TipoRelato.LENTO, 0)));

        EstimativaAtraso r = estimador.estimar(List.of(linha), false);

        assertTrue(r.impactos().get(0).divergencia());
        assertEquals(24, r.atrasoMinutos());
        assertTrue(r.rotaAlternativaRecomendada());
    }

    @Test
    void relatosDeLotacaoNaoContamComoLentidao() {
        SituacaoLinha linha = new SituacaoLinha(1L, "Linha 3-Vermelha", StatusLinha.NORMAL, List.of(
                new SituacaoLinha.RelatoAtivo(TipoRelato.LOTADO, 4),
                new SituacaoLinha.RelatoAtivo(TipoRelato.LOTADO, 4)));

        EstimativaAtraso r = estimador.estimar(List.of(linha), false);

        assertFalse(r.impactos().get(0).divergencia());
        assertEquals(10, r.chanceLentidaoPercentual());
    }

    @Test
    void relatosTemTetoDeMinutosPorLinha() {
        List<SituacaoLinha.RelatoAtivo> muitos = java.util.Collections.nCopies(20,
                new SituacaoLinha.RelatoAtivo(TipoRelato.PARADO, 10));
        SituacaoLinha linha = new SituacaoLinha(1L, "Linha 12-Safira", StatusLinha.NORMAL, muitos);

        assertEquals(EstimadorAtraso.MAX_MINUTOS_RELATOS, estimador.estimar(List.of(linha), false).atrasoMinutos());
    }

    private SituacaoLinha linha(StatusLinha status) {
        return new SituacaoLinha(1L, "Linha teste", status, List.of());
    }
}
