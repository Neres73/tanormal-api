package br.com.fiap.tanormal.bo;

import br.com.fiap.tanormal.dto.LinhaDto;
import br.com.fiap.tanormal.exception.RecursoNaoEncontradoException;
import br.com.fiap.tanormal.model.Estacao;
import br.com.fiap.tanormal.model.Linha;
import br.com.fiap.tanormal.model.StatusLinha;
import br.com.fiap.tanormal.dao.EstacaoDao;
import br.com.fiap.tanormal.dao.LinhaDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Linhas e estações (o "mapa" que o app conhece). */
@Service
public class LinhaBO {

    private final LinhaDao linhaDao;
    private final EstacaoDao estacaoDao;

    public LinhaBO(LinhaDao linhaDao, EstacaoDao estacaoDao) {
        this.linhaDao = linhaDao;
        this.estacaoDao = estacaoDao;
    }

    // ---------- Linhas ----------

    @Transactional(readOnly = true)
    public List<LinhaDto.Response> listar() {
        return linhaDao.findAllByOrderByNumeroAsc().stream().map(LinhaDto.Response::de).toList();
    }

    @Transactional(readOnly = true)
    public LinhaDto.Response buscar(Long id) {
        return LinhaDto.Response.de(obter(id));
    }

    @Transactional
    public LinhaDto.Response criar(LinhaDto.Request dados) {
        if (linhaDao.existsByNumero(dados.numero())) {
            throw new IllegalArgumentException("Já existe a linha " + dados.numero());
        }
        Linha linha = linhaDao.save(new Linha(dados.numero(), dados.nome(), dados.operadora()));
        return LinhaDto.Response.de(linha);
    }

    @Transactional
    public LinhaDto.Response atualizar(Long id, LinhaDto.Request dados) {
        Linha linha = obter(id);
        linhaDao.findByNumero(dados.numero())
                .filter(outra -> !outra.getId().equals(id))
                .ifPresent(outra -> {
                    throw new IllegalArgumentException("Já existe a linha " + dados.numero());
                });
        linha.atualizar(dados.numero(), dados.nome(), dados.operadora());
        return LinhaDto.Response.de(linha);
    }

    /** Chamado pelo leitor do site da operadora (scraping em Python) a cada poucos minutos. */
    @Transactional
    public LinhaDto.Response atualizarStatus(Long id, StatusLinha status) {
        Linha linha = obter(id);
        linha.atualizarStatus(status);
        return LinhaDto.Response.de(linha);
    }

    @Transactional
    public void excluir(Long id) {
        linhaDao.delete(obter(id));
    }

    // ---------- Estações ----------

    @Transactional(readOnly = true)
    public List<LinhaDto.EstacaoResponse> listarEstacoes(Long linhaId) {
        obter(linhaId);
        return estacaoDao.findByLinhaIdOrderByIdAsc(linhaId).stream()
                .map(LinhaDto.EstacaoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public LinhaDto.EstacaoResponse buscarEstacao(Long id) {
        return LinhaDto.EstacaoResponse.de(obterEstacao(id));
    }

    @Transactional
    public LinhaDto.EstacaoResponse criarEstacao(LinhaDto.EstacaoRequest dados) {
        Estacao estacao = estacaoDao.save(new Estacao(dados.nome(), obter(dados.linhaId())));
        return LinhaDto.EstacaoResponse.de(estacao);
    }

    @Transactional
    public LinhaDto.EstacaoResponse atualizarEstacao(Long id, LinhaDto.EstacaoRequest dados) {
        Estacao estacao = obterEstacao(id);
        estacao.atualizar(dados.nome(), obter(dados.linhaId()));
        return LinhaDto.EstacaoResponse.de(estacao);
    }

    @Transactional
    public void excluirEstacao(Long id) {
        estacaoDao.delete(obterEstacao(id));
    }

    Linha obter(Long id) {
        return linhaDao.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Linha", id));
    }

    Estacao obterEstacao(Long id) {
        return estacaoDao.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estação", id));
    }
}
