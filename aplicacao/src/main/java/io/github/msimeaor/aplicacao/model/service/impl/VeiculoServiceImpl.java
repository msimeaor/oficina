package io.github.msimeaor.aplicacao.model.service.impl;

import io.github.msimeaor.aplicacao.model.repository.VeiculoRepository;
import io.github.msimeaor.aplicacao.model.service.VeiculoService;
import org.springframework.stereotype.Service;

@Service
public class VeiculoServiceImpl implements VeiculoService {

  private VeiculoRepository repository;

  public VeiculoServiceImpl(VeiculoRepository repository) {
    this.repository = repository;
  }

}
