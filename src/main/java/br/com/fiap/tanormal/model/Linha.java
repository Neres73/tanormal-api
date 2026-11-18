package br.com.fiap.tanormal.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TN_LINHA")
public class Linha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_LINHA")
    private Long id;

    /** Número da linha (ex.: 12 para a 12-Safira). */
    @Column(name = "NR_LINHA", nullable = false, unique = true)
    private Integer numero;

    @Column(name = "NM_LINHA", nullable = false, length = 40)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "DS_OPERADORA", nullable = false, length = 20)
    private Operadora operadora;

    /** Último status lido do site da operadora. */
    @Enumerated(EnumType.STRING)
    @Column(name = "DS_STATUS_OFICIAL", nullable = false, length = 30)
    private StatusLinha statusOficial;

    @Column(name = "DT_ATUALIZACAO_STATUS")
    private LocalDateTime dataAtualizacaoStatus;

    protected Linha() {
    }

    public Linha(Integer numero, String nome, Operadora operadora) {
        this.numero = numero;
        this.nome = nome;
        this.operadora = operadora;
        this.statusOficial = StatusLinha.NORMAL;
        this.dataAtualizacaoStatus = LocalDateTime.now();
    }

    public void atualizar(Integer numero, String nome, Operadora operadora) {
        this.numero = numero;
        this.nome = nome;
        this.operadora = operadora;
    }

    public void atualizarStatus(StatusLinha novoStatus) {
        this.statusOficial = novoStatus;
        this.dataAtualizacaoStatus = LocalDateTime.now();
    }

    /** Ex.: "Linha 12-Safira". */
    public String getNomeCompleto() {
        return "Linha " + numero + "-" + nome;
    }

    public Long getId() {
        return id;
    }

    public Integer getNumero() {
        return numero;
    }

    public String getNome() {
        return nome;
    }

    public Operadora getOperadora() {
        return operadora;
    }

    public StatusLinha getStatusOficial() {
        return statusOficial;
    }

    public LocalDateTime getDataAtualizacaoStatus() {
        return dataAtualizacaoStatus;
    }
}
