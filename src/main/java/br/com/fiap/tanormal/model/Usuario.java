package br.com.fiap.tanormal.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TN_USUARIO")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_USUARIO")
    private Long id;

    @Column(name = "NM_USUARIO", nullable = false, length = 100)
    private String nome;

    @Column(name = "DS_EMAIL", nullable = false, unique = true, length = 120)
    private String email;

    @Column(name = "DT_CADASTRO", nullable = false)
    private LocalDateTime dataCadastro;

    protected Usuario() {
        // exigido pelo JPA
    }

    public Usuario(String nome, String email) {
        this.nome = nome;
        this.email = email;
        this.dataCadastro = LocalDateTime.now();
    }

    public void atualizar(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }
}
