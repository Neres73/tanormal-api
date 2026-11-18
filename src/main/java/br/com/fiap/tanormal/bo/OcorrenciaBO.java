package br.com.fiap.tanormal.bo;

import br.com.fiap.tanormal.dto.OcorrenciaDto;
import br.com.fiap.tanormal.exception.RecursoNaoEncontradoException;
import br.com.fiap.tanormal.model.FonteOcorrencia;
import br.com.fiap.tanormal.model.Linha;
import br.com.fiap.tanormal.model.Ocorrencia;
import br.com.fiap.tanormal.model.StatusLinha;
import br.com.fiap.tanormal.dao.OcorrenciaDao;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Histórico de ocorrências. Uma ocorrência OFICIAL também muda o status da linha;
 * quando a última ocorrência oficial aberta é encerrada, a linha volta ao normal.
 */
@Service
public class OcorrenciaBO {

    private final OcorrenciaDao dao;
    private final LinhaBO linhaBO;

    public OcorrenciaBO(OcorrenciaDao dao, LinhaBO linhaBO) {
        this.dao = dao;
        this.linhaBO = linhaBO;
    }

    @Transactional(readOnly = true)
    public List<OcorrenciaDto.Response> listar(Long linhaId, Boolean somenteAbertas) {
        List<Ocorrencia> ocorrencias;
        if (linhaId != null) {
            ocorrencias = dao.findByLinhaIdOrderByInicioDesc(linhaId);
        } else if (Boolean.TRUE.equals(somenteAbertas)) {
            ocorrencias = dao.findByFimIsNullOrderByInicioDesc();
        } else {
            ocorrencias = dao.findAll(Sort.by(Sort.Direction.DESC, "inicio"));
        }
        return ocorrencias.stream()
                .filter(o -> !Boolean.TRUE.equals(somenteAbertas) || o.isAberta())
                .map(OcorrenciaDto.Response::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public OcorrenciaDto.Response buscar(Long id) {
        return OcorrenciaDto.Response.de(obter(id));
    }

    @Transactional
    public OcorrenciaDto.Response criar(OcorrenciaDto.Request dados) {
        Linha linha = linhaBO.obter(dados.linhaId());
        Ocorrencia ocorrencia = new Ocorrencia(linha, dados.status(), dados.fonte(),
                dados.descricao(), dados.chuva(), dados.inicio());
        if (dados.fonte() == FonteOcorrencia.OFICIAL) {
            linha.atualizarStatus(dados.status());
        }
        return OcorrenciaDto.Response.de(dao.save(ocorrencia));
    }

    @Transactional
    public OcorrenciaDto.Response atualizar(Long id, OcorrenciaDto.Request dados) {
        Ocorrencia ocorrencia = obter(id);
        if (!ocorrencia.getLinha().getId().equals(dados.linhaId())) {
            throw new IllegalArgumentException("Não é possível mudar a linha de uma ocorrência; exclua e crie outra");
        }
        ocorrencia.atualizar(dados.status(), dados.descricao(), dados.chuva());
        if (ocorrencia.isAberta() && ocorrencia.getFonte() == FonteOcorrencia.OFICIAL) {
            ocorrencia.getLinha().atualizarStatus(dados.status());
        }
        return OcorrenciaDto.Response.de(ocorrencia);
    }

    @Transactional
    public OcorrenciaDto.Response encerrar(Long id, LocalDateTime fim) {
        Ocorrencia ocorrencia = obter(id);
        ocorrencia.encerrar(fim);
        if (ocorrencia.getFonte() == FonteOcorrencia.OFICIAL) {
            normalizarSeNaoHouverOutraAberta(ocorrencia);
        }
        return OcorrenciaDto.Response.de(ocorrencia);
    }

    @Transactional
    public void excluir(Long id) {
        Ocorrencia ocorrencia = obter(id);
        dao.delete(ocorrencia);
        dao.flush();
        if (ocorrencia.isAberta() && ocorrencia.getFonte() == FonteOcorrencia.OFICIAL) {
            normalizarSeNaoHouverOutraAberta(ocorrencia);
        }
    }

    private void normalizarSeNaoHouverOutraAberta(Ocorrencia encerrada) {
        Linha linha = encerrada.getLinha();
        boolean outraOficialAberta = dao.findByLinhaIdAndFimIsNull(linha.getId()).stream()
                .anyMatch(o -> o.getFonte() == FonteOcorrencia.OFICIAL && !o.getId().equals(encerrada.getId()));
        if (!outraOficialAberta) {
            linha.atualizarStatus(StatusLinha.NORMAL);
        }
    }

    private Ocorrencia obter(Long id) {
        return dao.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ocorrência", id));
    }
}
