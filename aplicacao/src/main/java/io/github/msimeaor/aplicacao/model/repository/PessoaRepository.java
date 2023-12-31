package io.github.msimeaor.aplicacao.model.repository;

import io.github.msimeaor.aplicacao.model.entity.Pessoa;
import jakarta.websocket.server.PathParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

  Optional<Pessoa> findByNome( String nome );

  @Query("SELECT p FROM Pessoa p WHERE p.nome LIKE :nome")
  Page<Pessoa> findByNomeLike( @PathParam("nome") String nome, Pageable pageable );

}
