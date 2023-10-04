package io.github.msimeaor.aplicacao.model.service;

import io.github.msimeaor.aplicacao.model.dto.request.TelefoneRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.TelefoneResponseDTO;
import org.springframework.http.ResponseEntity;

public interface TelefoneService {

  ResponseEntity<TelefoneResponseDTO> save( TelefoneRequestDTO telefoneRequest );

}
