package io.github.msimeaor.aplicacao.controller;

import io.github.msimeaor.aplicacao.model.service.impl.TelefoneServiceImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/telefones")
public class TelefoneRestController {

  private TelefoneServiceImpl service;

  public TelefoneRestController( TelefoneServiceImpl service ) {
    this.service = service;
  }

}
