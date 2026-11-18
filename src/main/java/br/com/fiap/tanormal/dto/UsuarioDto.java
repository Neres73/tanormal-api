package br.com.fiap.tanormal.dto;

import br.com.fiap.tanormal.model.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class UsuarioDto {

    private UsuarioDto() {
    }

    public record Request(
            @NotBlank @Size(max = 100) String nome,
            @NotBlank @Email @Size(max = 120) String email
    ) {
    }

    public record Response(Long id, String nome, String email, LocalDateTime dataCadastro) {

        public static Response de(Usuario u) {
            return new Response(u.getId(), u.getNome(), u.getEmail(), u.getDataCadastro());
        }
    }
}
