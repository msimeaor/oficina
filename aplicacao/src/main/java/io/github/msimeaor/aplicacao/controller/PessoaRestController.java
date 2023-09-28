package io.github.msimeaor.aplicacao.controller;

import io.github.msimeaor.aplicacao.model.service.impl.PessoaService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/pessoas")
public class PessoaRestController {

  private PessoaService service;

  public PessoaRestController( PessoaService service ) {
    this.service = service;
  }

  

}
