package br.com.fiap.tanormal.model;

/** O que o passageiro informa no app. O peso é quantos minutos o relato sugere de atraso. */
public enum TipoRelato {
    PARADO(8),
    LENTO(4),
    LOTADO(2);

    private final int pesoMinutos;

    TipoRelato(int pesoMinutos) {
        this.pesoMinutos = pesoMinutos;
    }

    public int getPesoMinutos() {
        return pesoMinutos;
    }
}
