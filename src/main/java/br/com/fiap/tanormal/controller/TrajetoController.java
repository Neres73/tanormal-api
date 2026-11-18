package br.com.fiap.tanormal.controller;

import br.com.fiap.tanormal.dto.AvisoDto;
import br.com.fiap.tanormal.dto.TrajetoDto;
import br.com.fiap.tanormal.bo.AvisoBO;
import br.com.fiap.tanormal.bo.TrajetoBO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/trajetos")
@Tag(name = "Trajetos e aviso de atraso")
public class TrajetoController {

    private final TrajetoBO bo;
    private final AvisoBO avisoBO;

    public TrajetoController(TrajetoBO bo, AvisoBO avisoBO) {
        this.bo = bo;
        this.avisoBO = avisoBO;
    }

    @GetMapping
    public List<TrajetoDto.Response> listar(@RequestParam(required = false) Long usuarioId) {
        return bo.listar(usuarioId);
    }

    @GetMapping("/{id}")
    public TrajetoDto.Response buscar(@PathVariable Long id) {
        return bo.buscar(id);
    }

    @GetMapping("/{id}/aviso")
    @Operation(summary = "Aviso de atraso do trajeto",
            description = "Junta o status oficial das linhas e os relatos dos últimos 40 min "
                    + "e estima quantos minutos a viagem deve atrasar.")
    public AvisoDto aviso(@PathVariable Long id,
                          @Parameter(description = "Está chovendo agora? (dado do INMET)")
                          @RequestParam(defaultValue = "false") boolean chuva) {
        return avisoBO.gerar(id, chuva);
    }

    @PostMapping
    public ResponseEntity<TrajetoDto.Response> criar(@RequestBody @Valid TrajetoDto.Request dados) {
        TrajetoDto.Response criado = bo.criar(dados);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri()).body(criado);
    }

    @PutMapping("/{id}")
    public TrajetoDto.Response atualizar(@PathVariable Long id, @RequestBody @Valid TrajetoDto.Request dados) {
        return bo.atualizar(id, dados);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
