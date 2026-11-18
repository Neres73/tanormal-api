package br.com.fiap.tanormal.model;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDateTime;

/** Relato de um passageiro ("parado", "lento", "lotado"), que outros podem confirmar. */
@Entity
@Table(name = "TN_RELATO")
public class Relato {

    /** Depois desse tempo o relato deixa de valer para a previsão do momento. */
    public static final Duration VALIDADE = Duration.ofMinutes(40);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_RELATO")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO", nullable = false)
    private Usuario usuario;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_LINHA", nullable = false)
    private Linha linha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ESTACAO")
    private Estacao estacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "DS_TIPO", nullable = false, length = 20)
    private TipoRelato tipo;

    @Column(name = "DS_COMENTARIO", length = 200)
    private String comentario;

    @Column(name = "DT_RELATO", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "NR_CONFIRMACOES", nullable = false)
    private Integer confirmacoes;

    protected Relato() {
    }

    public Relato(Usuario usuario, Linha linha, Estacao estacao, TipoRelato tipo, String comentario) {
        this.usuario = usuario;
        this.dataHora = LocalDateTime.now();
        this.confirmacoes = 0;
        atualizar(linha, estacao, tipo, comentario);
    }

    public void atualizar(Linha linha, Estacao estacao, TipoRelato tipo, String comentario) {
        if (estacao != null && !estacao.getLinha().getId().equals(linha.getId())) {
            throw new IllegalArgumentException("A estação informada não pertence a essa linha");
        }
        this.linha = linha;
        this.estacao = estacao;
        this.tipo = tipo;
        this.comentario = comentario;
    }

    /** Outro passageiro confirmou que está vendo a mesma coisa. */
    public void confirmar() {
        if (!isValido(LocalDateTime.now())) {
            throw new IllegalStateException("Esse relato já expirou e não pode mais ser confirmado");
        }
        this.confirmacoes++;
    }

    public boolean isValido(LocalDateTime agora) {
        return dataHora.plus(VALIDADE).isAfter(agora);
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Linha getLinha() {
        return linha;
    }

    public Estacao getEstacao() {
        return estacao;
    }

    public TipoRelato getTipo() {
        return tipo;
    }

    public String getComentario() {
        return comentario;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public Integer getConfirmacoes() {
        return confirmacoes;
    }
}
