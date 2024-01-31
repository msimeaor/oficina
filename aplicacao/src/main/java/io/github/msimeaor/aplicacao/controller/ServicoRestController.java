package io.github.msimeaor.aplicacao.controller;

import io.github.msimeaor.aplicacao.model.dto.request.ServicoRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.ServicoResponseDTO;
import io.github.msimeaor.aplicacao.model.service.impl.ServicoServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

  @GetMapping("/{id}")
  public ResponseEntity<ServicoResponseDTO> findById(@PathVariable("id") Long id) {
    return servicoService.findById(id);
  }

}
