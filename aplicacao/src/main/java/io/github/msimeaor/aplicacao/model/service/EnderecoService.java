package io.github.msimeaor.aplicacao.model.service;

import io.github.msimeaor.aplicacao.model.dto.request.EnderecoRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.EnderecoResponseDTO;
import org.springframework.http.ResponseEntity;

public interface EnderecoService {

  ResponseEntity<EnderecoResponseDTO> save(EnderecoRequestDTO enderecoRequest );

}
