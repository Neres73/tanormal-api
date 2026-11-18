package br.com.fiap.tanormal.bo;

import br.com.fiap.tanormal.dto.TrajetoDto;
import br.com.fiap.tanormal.exception.RecursoNaoEncontradoException;
import br.com.fiap.tanormal.model.Linha;
import br.com.fiap.tanormal.model.Trajeto;
import br.com.fiap.tanormal.dao.TrajetoDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrajetoBO {

    private final TrajetoDao dao;
    private final UsuarioBO usuarioBO;
    private final LinhaBO linhaBO;

    public TrajetoBO(TrajetoDao dao, UsuarioBO usuarioBO, LinhaBO linhaBO) {
        this.dao = dao;
        this.usuarioBO = usuarioBO;
        this.linhaBO = linhaBO;
    }

    @Transactional(readOnly = true)
    public List<TrajetoDto.Response> listar(Long usuarioId) {
        List<Trajeto> trajetos = usuarioId == null
                ? dao.findAll()
                : dao.findByUsuarioId(usuarioId);
        return trajetos.stream().map(TrajetoDto.Response::de).toList();
    }

    @Transactional(readOnly = true)
    public TrajetoDto.Response buscar(Long id) {
        return TrajetoDto.Response.de(obter(id));
    }

    @Transactional
    public TrajetoDto.Response criar(TrajetoDto.Request dados) {
        Trajeto trajeto = new Trajeto(
                usuarioBO.obter(dados.usuarioId()),
                dados.nome(),
                linhaBO.obterEstacao(dados.origemId()),
                linhaBO.obterEstacao(dados.destinoId()),
                linhas(dados.linhaIds()),
                dados.horarioSaida(),
                dados.tempoNormalMinutos());
        return TrajetoDto.Response.de(dao.save(trajeto));
    }

    /** O dono do trajeto não muda; o usuarioId do corpo é ignorado aqui. */
    @Transactional
    public TrajetoDto.Response atualizar(Long id, TrajetoDto.Request dados) {
        Trajeto trajeto = obter(id);
        trajeto.atualizar(
                dados.nome(),
                linhaBO.obterEstacao(dados.origemId()),
                linhaBO.obterEstacao(dados.destinoId()),
                linhas(dados.linhaIds()),
                dados.horarioSaida(),
                dados.tempoNormalMinutos());
        return TrajetoDto.Response.de(trajeto);
    }

    @Transactional
    public void excluir(Long id) {
        dao.delete(obter(id));
    }

    Trajeto obter(Long id) {
        return dao.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Trajeto", id));
    }

    private List<Linha> linhas(List<Long> ids) {
        if (ids.stream().distinct().count() != ids.size()) {
            throw new IllegalArgumentException("A mesma linha foi informada mais de uma vez");
        }
        return ids.stream().map(linhaBO::obter).toList();
    }
}
