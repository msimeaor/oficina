package io.github.msimeaor.aplicacao.controller;

import io.github.msimeaor.aplicacao.model.service.impl.EnderecoServiceImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enderecos")
public class EnderecoRestController {

  private EnderecoServiceImpl service;

  public EnderecoRestController( EnderecoServiceImpl service ) {
      this.service = service;
  }

}
