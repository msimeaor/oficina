package io.github.msimeaor.aplicacao.model.service;

import io.github.msimeaor.aplicacao.model.dto.request.VeiculoRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.VeiculoResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;

public interface VeiculoService {

  ResponseEntity<VeiculoResponseDTO> save(VeiculoRequestDTO veiculoRequestDTO);
  ResponseEntity<VeiculoResponseDTO> findById(Long id);
  ResponseEntity<PagedModel<EntityModel<VeiculoResponseDTO>>> findAll(Pageable pageable);

}
