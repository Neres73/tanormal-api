package br.com.fiap.tanormal.controller;

import br.com.fiap.tanormal.dto.RelatoDto;
import br.com.fiap.tanormal.bo.RelatoBO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/relatos")
@Tag(name = "Relatos dos passageiros")
public class RelatoController {

    private final RelatoBO bo;

    public RelatoController(RelatoBO bo) {
        this.bo = bo;
    }

    @GetMapping
    public List<RelatoDto.Response> listar(@RequestParam(required = false) Long linhaId) {
        return bo.listar(linhaId);
    }

    @GetMapping("/{id}")
    public RelatoDto.Response buscar(@PathVariable Long id) {
        return bo.buscar(id);
    }

    @PostMapping
    public ResponseEntity<RelatoDto.Response> criar(@RequestBody @Valid RelatoDto.Request dados) {
        RelatoDto.Response criado = bo.criar(dados);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri()).body(criado);
    }

    @PostMapping("/{id}/confirmacoes")
    @Operation(summary = "Outro passageiro confirma o relato")
    public RelatoDto.Response confirmar(@PathVariable Long id) {
        return bo.confirmar(id);
    }

    @PutMapping("/{id}")
    public RelatoDto.Response atualizar(@PathVariable Long id, @RequestBody @Valid RelatoDto.Request dados) {
        return bo.atualizar(id, dados);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
