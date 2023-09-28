package io.github.msimeaor.aplicacao.controller;

import io.github.msimeaor.aplicacao.model.dto.request.PessoaRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.PessoaResponseDTO;
import io.github.msimeaor.aplicacao.model.service.impl.PessoaServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/pessoas")
public class PessoaRestController {

  private PessoaServiceImpl service;

  public PessoaRestController( PessoaServiceImpl service ) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<PessoaResponseDTO> save(@RequestBody PessoaRequestDTO pessoaRequest ) {
    return service.save( pessoaRequest );
  }

}
