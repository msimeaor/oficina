package io.github.msimeaor.aplicacao.model.service;

import io.github.msimeaor.aplicacao.model.dto.request.PessoaRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.PessoaResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;

public interface PessoaService {

  ResponseEntity<PessoaResponseDTO> save( PessoaRequestDTO pessoaRequest, String placa );
  ResponseEntity<PessoaResponseDTO> findById( Long id );
  ResponseEntity<PagedModel<EntityModel<PessoaResponseDTO>>> findAll( Pageable pageable );
  ResponseEntity<PessoaResponseDTO> update( PessoaRequestDTO pessoaRequest, Long id );
  ResponseEntity<PagedModel<EntityModel<PessoaResponseDTO>>> findByNomeLike( String nome, Pageable pageable );

}
