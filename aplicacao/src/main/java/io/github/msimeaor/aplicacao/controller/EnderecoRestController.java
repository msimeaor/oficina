package io.github.msimeaor.aplicacao.controller;

import io.github.msimeaor.aplicacao.model.dto.request.EnderecoRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.EnderecoResponseDTO;
import io.github.msimeaor.aplicacao.model.service.impl.EnderecoServiceImpl;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

  @GetMapping("/{id}")
  public ResponseEntity<EnderecoResponseDTO> findById( @PathVariable("id") Long id ) {
    return service.findById(id);
  }

  @GetMapping
  public ResponseEntity<PagedModel<EntityModel<EnderecoResponseDTO>>> findAll(
          @RequestParam(name = "page", defaultValue = "0") Integer page,
          @RequestParam(name = "size", defaultValue = "10") Integer size,
          @RequestParam(name = "direction", defaultValue = "ASC") String direction
  ) {

    var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "id"));

    return service.findAll(pageable);
  }

  @PutMapping("/{id}")
  public ResponseEntity<EnderecoResponseDTO> update( @RequestBody @Valid EnderecoRequestDTO enderecoRequest,
                                                     @PathVariable("id") Long id ) {

    return service.update(enderecoRequest, id);

  }

}
