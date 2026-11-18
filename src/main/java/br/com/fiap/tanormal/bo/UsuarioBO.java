package br.com.fiap.tanormal.bo;

import br.com.fiap.tanormal.dto.UsuarioDto;
import br.com.fiap.tanormal.exception.RecursoNaoEncontradoException;
import br.com.fiap.tanormal.model.Usuario;
import br.com.fiap.tanormal.dao.UsuarioDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioBO {

    private final UsuarioDao dao;

    public UsuarioBO(UsuarioDao dao) {
        this.dao = dao;
    }

    @Transactional(readOnly = true)
    public List<UsuarioDto.Response> listar() {
        return dao.findAll().stream().map(UsuarioDto.Response::de).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioDto.Response buscar(Long id) {
        return UsuarioDto.Response.de(obter(id));
    }

    @Transactional
    public UsuarioDto.Response criar(UsuarioDto.Request dados) {
        if (dao.existsByEmailIgnoreCase(dados.email())) {
            throw new IllegalArgumentException("Já existe um usuário com esse e-mail");
        }
        Usuario usuario = dao.save(new Usuario(dados.nome(), dados.email()));
        return UsuarioDto.Response.de(usuario);
    }

    @Transactional
    public UsuarioDto.Response atualizar(Long id, UsuarioDto.Request dados) {
        Usuario usuario = obter(id);
        if (dao.existsByEmailIgnoreCaseAndIdNot(dados.email(), id)) {
            throw new IllegalArgumentException("Já existe um usuário com esse e-mail");
        }
        usuario.atualizar(dados.nome(), dados.email());
        return UsuarioDto.Response.de(usuario);
    }

    @Transactional
    public void excluir(Long id) {
        dao.delete(obter(id));
    }

    Usuario obter(Long id) {
        return dao.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));
    }
}
