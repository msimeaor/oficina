package io.github.msimeaor.aplicacao.model.service.impl;

import io.github.msimeaor.aplicacao.model.repository.ServicoRepository;
import io.github.msimeaor.aplicacao.model.service.ServicoService;
import org.springframework.stereotype.Service;

@Service
public class ServicoServiceImpl implements ServicoService {

  private ServicoRepository repository;

  public ServicoServiceImpl(ServicoRepository repository) {
    this.repository = repository;
  }

}
