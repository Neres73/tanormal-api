package br.com.fiap.tanormal.bo;

import br.com.fiap.tanormal.model.TipoRelato;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Serviço de domínio que estima o atraso de uma viagem juntando
 * o status oficial das linhas e os relatos dos passageiros.
 *
 * <p>É uma regra simples e explicável, para o app funcionar desde o primeiro dia.
 * Quando o modelo de regressão de Data Science estiver treinado com o histórico,
 * ele pode substituir esta classe sem mudar o resto da API.
 *
 * <p>Não acessa o banco: recebe dados prontos e devolve o cálculo,
 * por isso é fácil de testar.
 */
@Component
public class EstimadorAtraso {

    /** Teto de minutos que os relatos sozinhos podem somar numa linha. */
    static final int MAX_MINUTOS_RELATOS = 25;
    /** Cada confirmação aumenta o peso do relato, até esse limite. */
    static final double FATOR_MAXIMO_CONFIANCA = 3.0;
    /** A partir de quantos "passageiros equivalentes" falando de lentidão o status oficial é contestado. */
    static final double LIMIAR_DIVERGENCIA = 2.0;
    /** A partir desse atraso vale sugerir outro caminho. */
    static final int LIMIAR_ROTA_ALTERNATIVA = 15;
    static final double MULTIPLICADOR_CHUVA = 1.2;

    public EstimativaAtraso estimar(List<SituacaoLinha> linhas, boolean chovendo) {
        List<EstimativaAtraso.ImpactoLinha> impactos = new ArrayList<>();
        int atrasoTotal = 0;
        boolean algumaLinhaComProblemaOficial = false;
        double pesoLentidaoRelatada = 0;

        for (SituacaoLinha linha : linhas) {
            int minutosStatus = linha.statusOficial().getAtrasoBaseMinutos();
            int minutosRelatos = minutosPelosRelatos(linha.relatos());
            double lentidaoRelatada = pesoDeLentidao(linha.relatos());

            // Status e relatos descrevem o mesmo problema; não somamos os dois para não contar duas vezes.
            int atrasoLinha = Math.max(minutosStatus, minutosRelatos);
            boolean divergencia = linha.statusOficial().isNormal() && lentidaoRelatada >= LIMIAR_DIVERGENCIA;

            impactos.add(new EstimativaAtraso.ImpactoLinha(
                    linha.linhaId(),
                    linha.nomeLinha(),
                    linha.statusOficial(),
                    minutosStatus,
                    minutosRelatos,
                    linha.relatos().size(),
                    atrasoLinha,
                    divergencia));

            atrasoTotal += atrasoLinha;
            algumaLinhaComProblemaOficial |= !linha.statusOficial().isNormal();
            pesoLentidaoRelatada += lentidaoRelatada;
        }

        if (chovendo) {
            atrasoTotal = (int) Math.round(atrasoTotal * MULTIPLICADOR_CHUVA);
        }

        int chance = chanceDeLentidao(algumaLinhaComProblemaOficial, pesoLentidaoRelatada, chovendo);

        return new EstimativaAtraso(atrasoTotal, chance, atrasoTotal >= LIMIAR_ROTA_ALTERNATIVA, impactos);
    }

    private int minutosPelosRelatos(List<SituacaoLinha.RelatoAtivo> relatos) {
        double soma = 0;
        for (SituacaoLinha.RelatoAtivo relato : relatos) {
            soma += relato.tipo().getPesoMinutos() * fatorConfianca(relato.confirmacoes());
        }
        return (int) Math.min(Math.round(soma), MAX_MINUTOS_RELATOS);
    }

    /** Soma dos pesos só dos relatos que indicam trem lento ou parado (lotação não conta). */
    private double pesoDeLentidao(List<SituacaoLinha.RelatoAtivo> relatos) {
        double soma = 0;
        for (SituacaoLinha.RelatoAtivo relato : relatos) {
            if (relato.tipo() != TipoRelato.LOTADO) {
                soma += fatorConfianca(relato.confirmacoes());
            }
        }
        return soma;
    }

    /** Relato sozinho vale 1; cada confirmação soma 0,5, até 3. */
    private double fatorConfianca(int confirmacoes) {
        return Math.min(1 + 0.5 * confirmacoes, FATOR_MAXIMO_CONFIANCA);
    }

    private int chanceDeLentidao(boolean problemaOficial, double pesoLentidao, boolean chovendo) {
        int chance = problemaOficial ? 90 : (int) Math.min(10 + 15 * pesoLentidao, 85);
        if (chovendo) {
            chance += 10;
        }
        return Math.min(chance, 95);
    }
}
