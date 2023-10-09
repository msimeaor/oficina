package io.github.msimeaor.aplicacao.controller;

import io.github.msimeaor.aplicacao.model.dto.request.PessoaRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.PessoaResponseDTO;
import io.github.msimeaor.aplicacao.model.service.impl.PessoaServiceImpl;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/pessoas")
public class PessoaRestController {

  private PessoaServiceImpl service;

  public PessoaRestController( PessoaServiceImpl service ) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<PessoaResponseDTO> save(
          @RequestBody @Valid PessoaRequestDTO pessoaRequest,
          @RequestParam(name = "placa", required = true) String placa
  ) {
    return service.save( pessoaRequest, placa );
  }

  @GetMapping("/{id}")
  public ResponseEntity<PessoaResponseDTO> findById( @PathVariable("id") Long id ) {
    return service.findById( id );
  }

  @GetMapping
  public ResponseEntity<PagedModel<EntityModel<PessoaResponseDTO>>> findAll(
          @RequestParam(name = "page", defaultValue = "0") Integer page,
          @RequestParam(name = "size", defaultValue = "10") Integer size,
          @RequestParam(name = "direction", defaultValue = "ASC") String direction
  ) {

    var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "nome"));
    return service.findAll( pageable );
  }

  @PutMapping("/{id}")
  public ResponseEntity<PessoaResponseDTO> update( @RequestBody @Valid PessoaRequestDTO pessoaRequest,
                                                   @PathVariable Long id ) {
    return service.update(pessoaRequest, id);
  }

  @GetMapping("/findByNome")
  public ResponseEntity<PagedModel<EntityModel<PessoaResponseDTO>>> findByNome(
          @RequestParam(name = "nome", defaultValue = "") String nome,
          @RequestParam(name = "page", defaultValue = "0") Integer page,
          @RequestParam(name = "size", defaultValue = "5") Integer size,
          @RequestParam(name = "direction", defaultValue = "ASC") String direction
  ) {

    var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "nome"));

    return service.findByNomeLike( nome, pageable );
  }

}
