package br.com.fiap.tanormal.controller;

import br.com.fiap.tanormal.dto.LinhaDto;
import br.com.fiap.tanormal.bo.LinhaBO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/estacoes")
@Tag(name = "Estações")
public class EstacaoController {

    private final LinhaBO bo;

    public EstacaoController(LinhaBO bo) {
        this.bo = bo;
    }

    @GetMapping("/{id}")
    public LinhaDto.EstacaoResponse buscar(@PathVariable Long id) {
        return bo.buscarEstacao(id);
    }

    @PostMapping
    public ResponseEntity<LinhaDto.EstacaoResponse> criar(@RequestBody @Valid LinhaDto.EstacaoRequest dados) {
        LinhaDto.EstacaoResponse criada = bo.criarEstacao(dados);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criada.id()).toUri()).body(criada);
    }

    @PutMapping("/{id}")
    public LinhaDto.EstacaoResponse atualizar(@PathVariable Long id, @RequestBody @Valid LinhaDto.EstacaoRequest dados) {
        return bo.atualizarEstacao(id, dados);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluirEstacao(id);
        return ResponseEntity.noContent().build();
    }
}
