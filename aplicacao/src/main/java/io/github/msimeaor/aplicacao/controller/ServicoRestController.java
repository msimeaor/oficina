package io.github.msimeaor.aplicacao.controller;

import io.github.msimeaor.aplicacao.model.dto.request.ServicoRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.ServicoResponseDTO;
import io.github.msimeaor.aplicacao.model.service.impl.ServicoServiceImpl;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
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

  @GetMapping("/findByNome/{nome}")
  public ResponseEntity<PagedModel<EntityModel<ServicoResponseDTO>>> findByNome(
          @PathVariable("nome") String nome,
          @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
          @RequestParam(name = "size", defaultValue = "5", required = false) Integer size,
          @RequestParam(name = "direction", defaultValue = "ASC", required = false) String direction
  ) {

    var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "nome"));

    return servicoService.findByNome(nome, pageable);
  }

  @GetMapping()
  public ResponseEntity<PagedModel<EntityModel<ServicoResponseDTO>>> findAll(
          @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
          @RequestParam(name = "size", defaultValue = "5", required = false) Integer size,
          @RequestParam(name = "direction", defaultValue = "ASC", required = false) String direction
  ) {

    var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "nome"));

    return servicoService.findAll(pageable);
  }

}
