package io.github.msimeaor.aplicacao.model.service;

import io.github.msimeaor.aplicacao.model.dto.request.PessoaRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.PessoaResponseDTO;
import org.springframework.http.ResponseEntity;

public interface PessoaService {

  ResponseEntity<PessoaResponseDTO> save( PessoaRequestDTO pessoaRequest, String placa );
  ResponseEntity<PessoaResponseDTO> findById( Long id );

}
