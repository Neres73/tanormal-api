package br.com.fiap.tanormal.controller;

import br.com.fiap.tanormal.dto.LinhaDto;
import br.com.fiap.tanormal.bo.LinhaBO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/linhas")
@Tag(name = "Linhas")
public class LinhaController {

    private final LinhaBO bo;

    public LinhaController(LinhaBO bo) {
        this.bo = bo;
    }

    @GetMapping
    public List<LinhaDto.Response> listar() {
        return bo.listar();
    }

    @GetMapping("/{id}")
    public LinhaDto.Response buscar(@PathVariable Long id) {
        return bo.buscar(id);
    }

    @GetMapping("/{id}/estacoes")
    public List<LinhaDto.EstacaoResponse> estacoes(@PathVariable Long id) {
        return bo.listarEstacoes(id);
    }

    @PostMapping
    public ResponseEntity<LinhaDto.Response> criar(@RequestBody @Valid LinhaDto.Request dados) {
        LinhaDto.Response criada = bo.criar(dados);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criada.id()).toUri()).body(criada);
    }

    @PutMapping("/{id}")
    public LinhaDto.Response atualizar(@PathVariable Long id, @RequestBody @Valid LinhaDto.Request dados) {
        return bo.atualizar(id, dados);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualiza o status oficial (usado pelo leitor do site da operadora)")
    public LinhaDto.Response atualizarStatus(@PathVariable Long id, @RequestBody @Valid LinhaDto.StatusRequest dados) {
        return bo.atualizarStatus(id, dados.status());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
