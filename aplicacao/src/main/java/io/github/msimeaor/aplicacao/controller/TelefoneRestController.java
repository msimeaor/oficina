package io.github.msimeaor.aplicacao.controller;

import io.github.msimeaor.aplicacao.model.dto.request.TelefoneRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.TelefoneResponseDTO;
import io.github.msimeaor.aplicacao.model.service.impl.TelefoneServiceImpl;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

  @GetMapping("/{id}")
  public ResponseEntity<TelefoneResponseDTO> findById( @PathVariable("id") Long id ) {
    return service.findById(id);
  }

  @GetMapping
  public ResponseEntity<PagedModel<EntityModel<TelefoneResponseDTO>>> findAll(
          @RequestParam(name = "page", defaultValue = "0") Integer page,
          @RequestParam(name = "size", defaultValue = "10") Integer size,
          @RequestParam(name = "direction", defaultValue = "ASC") String direction
  ) {

    var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "numero"));

    return service.findAll(pageable);
  }

  @PutMapping("/{id}")
  public ResponseEntity<TelefoneResponseDTO> update( @RequestBody @Valid TelefoneRequestDTO telefoneRequest,
                                                     @PathVariable("id") Long id) {

    return service.update(telefoneRequest, id);
  }

}
