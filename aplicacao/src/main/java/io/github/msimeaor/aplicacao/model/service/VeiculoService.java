package io.github.msimeaor.aplicacao.model.service;

import io.github.msimeaor.aplicacao.model.dto.request.VeiculoRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.VeiculoResponseDTO;
import org.springframework.http.ResponseEntity;

public interface VeiculoService {

  ResponseEntity<VeiculoResponseDTO> save(VeiculoRequestDTO veiculoRequestDTO);
  ResponseEntity<VeiculoResponseDTO> findById(Long id);

}
