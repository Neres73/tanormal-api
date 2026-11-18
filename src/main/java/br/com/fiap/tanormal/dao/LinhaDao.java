package br.com.fiap.tanormal.dao;

import br.com.fiap.tanormal.model.Linha;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LinhaDao extends JpaRepository<Linha, Long> {

    Optional<Linha> findByNumero(Integer numero);

    boolean existsByNumero(Integer numero);

    List<Linha> findAllByOrderByNumeroAsc();
}
