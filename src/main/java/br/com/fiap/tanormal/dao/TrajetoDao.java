package br.com.fiap.tanormal.dao;

import br.com.fiap.tanormal.model.Trajeto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrajetoDao extends JpaRepository<Trajeto, Long> {

    List<Trajeto> findByUsuarioId(Long usuarioId);
}
