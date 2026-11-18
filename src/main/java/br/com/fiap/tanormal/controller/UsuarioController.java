package br.com.fiap.tanormal.controller;

import br.com.fiap.tanormal.dto.UsuarioDto;
import br.com.fiap.tanormal.bo.UsuarioBO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários")
public class UsuarioController {

    private final UsuarioBO bo;

    public UsuarioController(UsuarioBO bo) {
        this.bo = bo;
    }

    @GetMapping
    public List<UsuarioDto.Response> listar() {
        return bo.listar();
    }

    @GetMapping("/{id}")
    public UsuarioDto.Response buscar(@PathVariable Long id) {
        return bo.buscar(id);
    }

    @PostMapping
    public ResponseEntity<UsuarioDto.Response> criar(@RequestBody @Valid UsuarioDto.Request dados) {
        UsuarioDto.Response criado = bo.criar(dados);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri()).body(criado);
    }

    @PutMapping("/{id}")
    public UsuarioDto.Response atualizar(@PathVariable Long id, @RequestBody @Valid UsuarioDto.Request dados) {
        return bo.atualizar(id, dados);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
