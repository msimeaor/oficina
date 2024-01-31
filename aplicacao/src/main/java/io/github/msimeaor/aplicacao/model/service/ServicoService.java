package io.github.msimeaor.aplicacao.model.service;

import io.github.msimeaor.aplicacao.model.dto.request.ServicoRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.ServicoResponseDTO;
import org.springframework.http.ResponseEntity;

public interface ServicoService {

  ResponseEntity<ServicoResponseDTO> save(ServicoRequestDTO servicoRequestDTO);
  ResponseEntity<ServicoResponseDTO> findById(Long id);

}
