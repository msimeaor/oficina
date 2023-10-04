package io.github.msimeaor.aplicacao.controller;

import io.github.msimeaor.aplicacao.model.dto.request.TelefoneRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.TelefoneResponseDTO;
import io.github.msimeaor.aplicacao.model.service.impl.TelefoneServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/telefones")
public class TelefoneRestController {

  private TelefoneServiceImpl service;

  public TelefoneRestController( TelefoneServiceImpl service ) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<TelefoneResponseDTO> save( @RequestBody @Valid TelefoneRequestDTO telefoneRequest ) {
    return service.save(telefoneRequest);
  }

}
