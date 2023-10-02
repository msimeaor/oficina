package io.github.msimeaor.aplicacao.controller;

import io.github.msimeaor.aplicacao.model.dto.request.PessoaRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.PessoaResponseDTO;
import io.github.msimeaor.aplicacao.model.service.impl.PessoaServiceImpl;
import jakarta.validation.Valid;
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

}
