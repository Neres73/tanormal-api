package br.com.fiap.tanormal.dao;

import br.com.fiap.tanormal.model.Relato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface RelatoDao extends JpaRepository<Relato, Long> {

    List<Relato> findByLinhaIdOrderByDataHoraDesc(Long linhaId);

    /** Relatos ainda válidos das linhas de um trajeto. */
    List<Relato> findByLinhaIdInAndDataHoraAfter(Collection<Long> linhaIds, LocalDateTime desde);
}
