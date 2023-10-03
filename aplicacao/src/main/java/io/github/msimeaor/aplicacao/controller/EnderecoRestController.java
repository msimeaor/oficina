package io.github.msimeaor.aplicacao.controller;

import io.github.msimeaor.aplicacao.model.dto.request.EnderecoRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.EnderecoResponseDTO;
import io.github.msimeaor.aplicacao.model.service.impl.EnderecoServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enderecos")
public class EnderecoRestController {

  private EnderecoServiceImpl service;

  public EnderecoRestController( EnderecoServiceImpl service ) {
      this.service = service;
  }

  @PostMapping
  public ResponseEntity<EnderecoResponseDTO> save( @RequestBody @Valid EnderecoRequestDTO enderecoRequest ) {
    return service.save(enderecoRequest);
  }

}
