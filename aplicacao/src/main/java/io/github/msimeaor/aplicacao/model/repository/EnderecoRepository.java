package io.github.msimeaor.aplicacao.model.repository;

import io.github.msimeaor.aplicacao.model.entity.Endereco;
import jakarta.websocket.server.PathParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

  Endereco findByLogradouro( String logradouro );

  @Query("SELECT e FROM Endereco e WHERE e.logradouro LIKE :logradouro")
  Page<Endereco> findByLogradouro(@PathParam("logradouro") String logradouro, Pageable pageable);

}
