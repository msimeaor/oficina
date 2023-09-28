package io.github.msimeaor.aplicacao.model.service.impl;

import io.github.msimeaor.aplicacao.model.repository.PessoaRepository;
import org.springframework.stereotype.Service;

@Service
public class PessoaService {

  public PessoaRepository repository;

  public PessoaService( PessoaRepository repository ) {
    this.repository = repository;
  }

}
