package br.com.fiap.tanormal.dao;

import br.com.fiap.tanormal.model.Estacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstacaoDao extends JpaRepository<Estacao, Long> {

    List<Estacao> findByLinhaIdOrderByIdAsc(Long linhaId);
}
