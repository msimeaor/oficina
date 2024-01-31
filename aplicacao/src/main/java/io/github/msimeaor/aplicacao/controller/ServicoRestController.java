package io.github.msimeaor.aplicacao.controller;

import io.github.msimeaor.aplicacao.model.service.impl.ServicoServiceImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/servicos")
public class ServicoRestController {

  private ServicoServiceImpl servicoService;

  public ServicoRestController(ServicoServiceImpl servicoService) {
    this.servicoService = servicoService;
  }

}
