package br.com.fiap.tanormal.controller;

import br.com.fiap.tanormal.bo.OcorrenciaBO;
import br.com.fiap.tanormal.dto.OcorrenciaDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/ocorrencias")
@Tag(name = "Ocorrências")
public class OcorrenciaController {

    private final OcorrenciaBO bo;

    public OcorrenciaController(OcorrenciaBO bo) {
        this.bo = bo;
    }

    @GetMapping
    public List<OcorrenciaDto.Response> listar(@RequestParam(required = false) Long linhaId,
                                               @RequestParam(required = false) Boolean abertas) {
        return bo.listar(linhaId, abertas);
    }

    @GetMapping("/{id}")
    public OcorrenciaDto.Response buscar(@PathVariable Long id) {
        return bo.buscar(id);
    }

    @PostMapping
    @Operation(summary = "Registra uma ocorrência (se for OFICIAL, também muda o status da linha)")
    public ResponseEntity<OcorrenciaDto.Response> criar(@RequestBody @Valid OcorrenciaDto.Request dados) {
        OcorrenciaDto.Response criada = bo.criar(dados);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criada.id()).toUri()).body(criada);
    }

    @PutMapping("/{id}")
    public OcorrenciaDto.Response atualizar(@PathVariable Long id, @RequestBody @Valid OcorrenciaDto.Request dados) {
        return bo.atualizar(id, dados);
    }

    @PatchMapping("/{id}/encerramento")
    @Operation(summary = "Encerra a ocorrência (sem corpo, encerra agora)")
    public OcorrenciaDto.Response encerrar(@PathVariable Long id,
                                           @RequestBody(required = false) @Valid OcorrenciaDto.EncerramentoRequest dados) {
        return bo.encerrar(id, dados != null ? dados.fim() : null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
