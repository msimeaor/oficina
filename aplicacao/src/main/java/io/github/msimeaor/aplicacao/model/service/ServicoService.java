package io.github.msimeaor.aplicacao.model.service;

import io.github.msimeaor.aplicacao.model.dto.request.ServicoRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.ServicoResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;

public interface ServicoService {

  ResponseEntity<ServicoResponseDTO> save(ServicoRequestDTO servicoRequestDTO);
  ResponseEntity<ServicoResponseDTO> findById(Long id);
  ResponseEntity<PagedModel<EntityModel<ServicoResponseDTO>>> findByNome(String nome, Pageable pageable);
  ResponseEntity<PagedModel<EntityModel<ServicoResponseDTO>>> findAll(Pageable pageable);
  ResponseEntity<ServicoResponseDTO> update(ServicoRequestDTO servicoRequestDTO, Long id);

}
