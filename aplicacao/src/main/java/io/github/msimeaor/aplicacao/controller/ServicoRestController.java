package io.github.msimeaor.aplicacao.controller;

import io.github.msimeaor.aplicacao.model.dto.request.ServicoRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.ServicoResponseDTO;
import io.github.msimeaor.aplicacao.model.service.impl.ServicoServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/servicos")
public class ServicoRestController {

  private ServicoServiceImpl servicoService;

  public ServicoRestController(ServicoServiceImpl servicoService) {
    this.servicoService = servicoService;
  }

  @PostMapping()
  public ResponseEntity<ServicoResponseDTO> save(@Valid @RequestBody ServicoRequestDTO servicoRequestDTO) {
    return servicoService.save(servicoRequestDTO);
  }

}
