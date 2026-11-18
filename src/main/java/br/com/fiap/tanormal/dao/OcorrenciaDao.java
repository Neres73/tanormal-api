package br.com.fiap.tanormal.dao;

import br.com.fiap.tanormal.model.Ocorrencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface OcorrenciaDao extends JpaRepository<Ocorrencia, Long> {

    List<Ocorrencia> findByLinhaIdOrderByInicioDesc(Long linhaId);

    List<Ocorrencia> findByFimIsNullOrderByInicioDesc();

    List<Ocorrencia> findByLinhaIdAndFimIsNull(Long linhaId);

    List<Ocorrencia> findByLinhaIdInAndFimIsNull(Collection<Long> linhaIds);
}
