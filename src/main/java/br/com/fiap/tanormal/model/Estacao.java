package br.com.fiap.tanormal.model;

import jakarta.persistence.*;

/**
 * Estação de uma linha. Estações de integração (ex.: Brás) aparecem uma vez
 * para cada linha que passa por elas, o que simplifica o grafo da rota B.
 */
@Entity
@Table(name = "TN_ESTACAO")
public class Estacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ESTACAO")
    private Long id;

    @Column(name = "NM_ESTACAO", nullable = false, length = 80)
    private String nome;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_LINHA", nullable = false)
    private Linha linha;

    protected Estacao() {
    }

    public Estacao(String nome, Linha linha) {
        this.nome = nome;
        this.linha = linha;
    }

    public void atualizar(String nome, Linha linha) {
        this.nome = nome;
        this.linha = linha;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Linha getLinha() {
        return linha;
    }
}
