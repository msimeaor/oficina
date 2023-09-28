package io.github.msimeaor.aplicacao.model.service.impl;

import io.github.msimeaor.aplicacao.model.dto.request.PessoaRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.PessoaResponseDTO;
import io.github.msimeaor.aplicacao.model.repository.PessoaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PessoaServiceImpl {

  public PessoaRepository repository;

  public PessoaServiceImpl(PessoaRepository repository ) {
    this.repository = repository;
  }

  public ResponseEntity<PessoaResponseDTO> save(PessoaRequestDTO pessoaRequest ) {
    return null;
  }
}
