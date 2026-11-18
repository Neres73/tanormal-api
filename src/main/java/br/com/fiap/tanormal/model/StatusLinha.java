package br.com.fiap.tanormal.model;

/**
 * Status que as operadoras publicam no site.
 * O atraso base é quanto, em média, cada status costuma somar na viagem
 * (valor inicial; o modelo de Data Science pode recalibrar com o histórico).
 */
public enum StatusLinha {
    NORMAL("Operação normal", 0),
    VELOCIDADE_REDUZIDA("Velocidade reduzida", 10),
    OPERACAO_PARCIAL("Operação parcial", 20),
    PARALISADA("Paralisada", 40);

    private final String descricao;
    private final int atrasoBaseMinutos;

    StatusLinha(String descricao, int atrasoBaseMinutos) {
        this.descricao = descricao;
        this.atrasoBaseMinutos = atrasoBaseMinutos;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getAtrasoBaseMinutos() {
        return atrasoBaseMinutos;
    }

    public boolean isNormal() {
        return this == NORMAL;
    }
}
