package io.github.msimeaor.aplicacao.model.service.impl;

import io.github.msimeaor.aplicacao.model.repository.EnderecoRepository;
import io.github.msimeaor.aplicacao.model.service.EnderecoService;
import org.springframework.stereotype.Service;

@Service
public class EnderecoServiceImpl implements EnderecoService {

  private EnderecoRepository repository;

  public EnderecoServiceImpl( EnderecoRepository repository ) {
    this.repository = repository;
  }

}
