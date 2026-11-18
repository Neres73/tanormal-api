package br.com.fiap.tanormal.model;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Problema registrado numa linha, com início e fim.
 * É esse histórico que o Metrô não divulga e que o app vai guardando
 * para o modelo de Data Science treinar.
 */
@Entity
@Table(name = "TN_OCORRENCIA")
public class Ocorrencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_OCORRENCIA")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_LINHA", nullable = false)
    private Linha linha;

    @Enumerated(EnumType.STRING)
    @Column(name = "DS_STATUS", nullable = false, length = 30)
    private StatusLinha status;

    @Enumerated(EnumType.STRING)
    @Column(name = "DS_FONTE", nullable = false, length = 20)
    private FonteOcorrencia fonte;

    @Column(name = "DS_OCORRENCIA", length = 300)
    private String descricao;

    @Column(name = "ST_CHUVA", nullable = false)
    private Boolean chuva;

    @Column(name = "DT_INICIO", nullable = false)
    private LocalDateTime inicio;

    @Column(name = "DT_FIM")
    private LocalDateTime fim;

    protected Ocorrencia() {
    }

    public Ocorrencia(Linha linha, StatusLinha status, FonteOcorrencia fonte,
                      String descricao, Boolean chuva, LocalDateTime inicio) {
        if (status.isNormal()) {
            throw new IllegalArgumentException("Uma ocorrência precisa ter um status diferente de NORMAL");
        }
        this.linha = linha;
        this.status = status;
        this.fonte = fonte;
        this.descricao = descricao;
        this.chuva = chuva != null && chuva;
        this.inicio = inicio != null ? inicio : LocalDateTime.now();
    }

    public void atualizar(StatusLinha status, String descricao, Boolean chuva) {
        if (status.isNormal()) {
            throw new IllegalArgumentException("Para normalizar a linha, encerre a ocorrência");
        }
        this.status = status;
        this.descricao = descricao;
        this.chuva = chuva != null && chuva;
    }

    public void encerrar(LocalDateTime quando) {
        if (!isAberta()) {
            throw new IllegalStateException("Essa ocorrência já foi encerrada");
        }
        LocalDateTime fimInformado = quando != null ? quando : LocalDateTime.now();
        if (fimInformado.isBefore(inicio)) {
            throw new IllegalArgumentException("O fim não pode ser antes do início da ocorrência");
        }
        this.fim = fimInformado;
    }

    public boolean isAberta() {
        return fim == null;
    }

    /** Duração em minutos (até agora, se ainda estiver aberta). */
    public long getDuracaoMinutos() {
        LocalDateTime ate = fim != null ? fim : LocalDateTime.now();
        return Duration.between(inicio, ate).toMinutes();
    }

    public Long getId() {
        return id;
    }

    public Linha getLinha() {
        return linha;
    }

    public StatusLinha getStatus() {
        return status;
    }

    public FonteOcorrencia getFonte() {
        return fonte;
    }

    public String getDescricao() {
        return descricao;
    }

    public Boolean getChuva() {
        return chuva;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public LocalDateTime getFim() {
        return fim;
    }
}
