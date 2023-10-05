package io.github.msimeaor.aplicacao.model.service;

import io.github.msimeaor.aplicacao.model.dto.request.TelefoneRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.TelefoneResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;

public interface TelefoneService {

  ResponseEntity<TelefoneResponseDTO> save( TelefoneRequestDTO telefoneRequest );
  ResponseEntity<TelefoneResponseDTO> findById( Long id );
  ResponseEntity<PagedModel<EntityModel<TelefoneResponseDTO>>> findAll( Pageable pageable );
  ResponseEntity<TelefoneResponseDTO> update( TelefoneRequestDTO telefoneRequest, Long id );

}
