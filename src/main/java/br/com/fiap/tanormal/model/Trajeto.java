package br.com.fiap.tanormal.model;

import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/** Trajeto do dia a dia cadastrado pelo passageiro (ex.: Itaim Paulista → Sé, 6h30). */
@Entity
@Table(name = "TN_TRAJETO")
public class Trajeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TRAJETO")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO", nullable = false)
    private Usuario usuario;

    @Column(name = "NM_TRAJETO", nullable = false, length = 60)
    private String nome;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ESTACAO_ORIGEM", nullable = false)
    private Estacao origem;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ESTACAO_DESTINO", nullable = false)
    private Estacao destino;

    /** Linhas usadas no caminho, na ordem da viagem. */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "TN_TRAJETO_LINHA",
            joinColumns = @JoinColumn(name = "ID_TRAJETO"),
            inverseJoinColumns = @JoinColumn(name = "ID_LINHA"))
    @OrderColumn(name = "NR_ORDEM")
    private List<Linha> linhas = new ArrayList<>();

    @Column(name = "HR_SAIDA", nullable = false)
    private LocalTime horarioSaida;

    /** Quanto a viagem leva num dia sem problema. */
    @Column(name = "NR_TEMPO_NORMAL_MIN", nullable = false)
    private Integer tempoNormalMinutos;

    protected Trajeto() {
    }

    public Trajeto(Usuario usuario, String nome, Estacao origem, Estacao destino,
                   List<Linha> linhas, LocalTime horarioSaida, Integer tempoNormalMinutos) {
        this.usuario = usuario;
        atualizar(nome, origem, destino, linhas, horarioSaida, tempoNormalMinutos);
    }

    public void atualizar(String nome, Estacao origem, Estacao destino, List<Linha> linhas,
                          LocalTime horarioSaida, Integer tempoNormalMinutos) {
        if (origem.getId().equals(destino.getId())) {
            throw new IllegalArgumentException("Origem e destino não podem ser a mesma estação");
        }
        if (!contemLinha(linhas, origem.getLinha()) || !contemLinha(linhas, destino.getLinha())) {
            throw new IllegalArgumentException(
                    "As linhas do trajeto precisam incluir a linha da estação de origem e a da estação de destino");
        }
        this.nome = nome;
        this.origem = origem;
        this.destino = destino;
        this.linhas.clear();
        this.linhas.addAll(linhas);
        this.horarioSaida = horarioSaida;
        this.tempoNormalMinutos = tempoNormalMinutos;
    }

    private static boolean contemLinha(List<Linha> linhas, Linha procurada) {
        return linhas.stream().anyMatch(l -> l.getId().equals(procurada.getId()));
    }

    public LocalTime getHorarioChegadaPrevisto() {
        return horarioSaida.plusMinutes(tempoNormalMinutos);
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getNome() {
        return nome;
    }

    public Estacao getOrigem() {
        return origem;
    }

    public Estacao getDestino() {
        return destino;
    }

    public List<Linha> getLinhas() {
        return linhas;
    }

    public LocalTime getHorarioSaida() {
        return horarioSaida;
    }

    public Integer getTempoNormalMinutos() {
        return tempoNormalMinutos;
    }
}
