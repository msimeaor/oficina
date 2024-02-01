package io.github.msimeaor.aplicacao.model.repository;

import io.github.msimeaor.aplicacao.model.entity.Servico;
import jakarta.websocket.server.PathParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {

  Optional<Servico> findByNome(String nome);

  @Query("SELECT s FROM Servico s WHERE s.nome LIKE :nome")
  Page<Servico> findByNome(@PathParam("nome") String nome, Pageable pageable);

}
