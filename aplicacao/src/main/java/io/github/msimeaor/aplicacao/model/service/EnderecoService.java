package io.github.msimeaor.aplicacao.model.service;

import io.github.msimeaor.aplicacao.model.dto.request.EnderecoRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.EnderecoResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;

public interface EnderecoService {

  ResponseEntity<EnderecoResponseDTO> save( EnderecoRequestDTO enderecoRequest );
  ResponseEntity<EnderecoResponseDTO> findById(Long id);
  ResponseEntity<PagedModel<EntityModel<EnderecoResponseDTO>>> findAll( Pageable pageable );
  ResponseEntity<EnderecoResponseDTO> update( EnderecoRequestDTO enderecoRequest, Long id );
  ResponseEntity<PagedModel<EntityModel<EnderecoResponseDTO>>> findByLogradouro(String logradouro, Pageable pageable);

}
