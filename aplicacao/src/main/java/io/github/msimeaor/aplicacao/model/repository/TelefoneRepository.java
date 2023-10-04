package io.github.msimeaor.aplicacao.model.repository;

import io.github.msimeaor.aplicacao.model.entity.Telefone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelefoneRepository extends JpaRepository<Telefone, Long> {
}
