package io.github.msimeaor.aplicacao.model.service.impl;

import io.github.msimeaor.aplicacao.model.repository.TelefoneRepository;
import io.github.msimeaor.aplicacao.model.service.TelefoneService;
import org.springframework.stereotype.Service;
@Service
public class TelefoneServiceImpl implements TelefoneService {

  private TelefoneRepository repository;

  public TelefoneServiceImpl( TelefoneRepository repository ) {
    this.repository = repository;
  }

}
