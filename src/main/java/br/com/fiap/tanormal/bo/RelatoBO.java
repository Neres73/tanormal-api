package br.com.fiap.tanormal.bo;

import br.com.fiap.tanormal.dto.RelatoDto;
import br.com.fiap.tanormal.exception.RecursoNaoEncontradoException;
import br.com.fiap.tanormal.model.Estacao;
import br.com.fiap.tanormal.model.Relato;
import br.com.fiap.tanormal.dao.RelatoDao;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RelatoBO {

    private final RelatoDao dao;
    private final UsuarioBO usuarioBO;
    private final LinhaBO linhaBO;

    public RelatoBO(RelatoDao dao, UsuarioBO usuarioBO, LinhaBO linhaBO) {
        this.dao = dao;
        this.usuarioBO = usuarioBO;
        this.linhaBO = linhaBO;
    }

    @Transactional(readOnly = true)
    public List<RelatoDto.Response> listar(Long linhaId) {
        List<Relato> relatos = linhaId == null
                ? dao.findAll(Sort.by(Sort.Direction.DESC, "dataHora"))
                : dao.findByLinhaIdOrderByDataHoraDesc(linhaId);
        return relatos.stream().map(RelatoDto.Response::de).toList();
    }

    @Transactional(readOnly = true)
    public RelatoDto.Response buscar(Long id) {
        return RelatoDto.Response.de(obter(id));
    }

    @Transactional
    public RelatoDto.Response criar(RelatoDto.Request dados) {
        Relato relato = new Relato(
                usuarioBO.obter(dados.usuarioId()),
                linhaBO.obter(dados.linhaId()),
                estacao(dados.estacaoId()),
                dados.tipo(),
                dados.comentario());
        return RelatoDto.Response.de(dao.save(relato));
    }

    @Transactional
    public RelatoDto.Response atualizar(Long id, RelatoDto.Request dados) {
        Relato relato = obter(id);
        relato.atualizar(linhaBO.obter(dados.linhaId()), estacao(dados.estacaoId()),
                dados.tipo(), dados.comentario());
        return RelatoDto.Response.de(relato);
    }

    /** "Eu também estou vendo isso": aumenta o peso do relato na previsão. */
    @Transactional
    public RelatoDto.Response confirmar(Long id) {
        Relato relato = obter(id);
        relato.confirmar();
        return RelatoDto.Response.de(relato);
    }

    @Transactional
    public void excluir(Long id) {
        dao.delete(obter(id));
    }

    private Relato obter(Long id) {
        return dao.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Relato", id));
    }

    private Estacao estacao(Long estacaoId) {
        return estacaoId == null ? null : linhaBO.obterEstacao(estacaoId);
    }
}
