package io.github.msimeaor.aplicacao.controller;

import io.github.msimeaor.aplicacao.exceptions.ExceptionResponse;
import io.github.msimeaor.aplicacao.exceptions.pessoa.PessoaNotFoundException;
import io.github.msimeaor.aplicacao.model.dto.request.PessoaRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.PessoaResponseDTO;
import io.github.msimeaor.aplicacao.model.service.impl.PessoaServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/pessoas")
@Tag(name = "Pessoa Rest Controller")
public class PessoaRestController {

  private PessoaServiceImpl service;

  public PessoaRestController( PessoaServiceImpl service ) {
    this.service = service;
  }

  @PostMapping
  @Operation(summary = "Save a person in the database", description = "Save a person in the database",
    tags = {"Save"},
    responses = {
      @ApiResponse(description = "Success", responseCode = "201",
        content = {
          @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = PessoaResponseDTO.class)
          )
        }
      ),
      @ApiResponse(description = "Person with personal data already registered", responseCode = "409",
        content = {
          @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ExceptionResponse.class)
          )
        }
      ),
      @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
      @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
      @ApiResponse(description = "Forbiden", responseCode = "403", content = @Content),
      @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
    }
  )
  public ResponseEntity<PessoaResponseDTO> save(
          @RequestBody @Valid PessoaRequestDTO pessoaRequest,
          @RequestParam(name = "placa", required = true) String placa
  ) {
    return service.save( pessoaRequest, placa );
  }

  @GetMapping("/{id}")
  @Operation(summary = "Find a person in database by ID", description = "Find a person in database by ID",
    tags = {"Find"},
    responses = {
      @ApiResponse(description = "Success", responseCode = "200",
        content = {
          @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = PessoaResponseDTO.class)
          )
        }
      ),
      @ApiResponse(description = "Person not found in the database", responseCode = "404",
        content = {
          @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ExceptionResponse.class)
          )
        }
      ),
      @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
      @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
      @ApiResponse(description = "Forbiden", responseCode = "403", content = @Content),
      @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
    }
  )
  public ResponseEntity<PessoaResponseDTO> findById( @PathVariable("id") Long id ) {
    return service.findById( id );
  }

  @GetMapping
  @Operation(summary = "Find all people in the database", description = "Find all people in the database",
    tags = {"Find"},
    responses = {
      @ApiResponse(description = "Success", responseCode = "200",
        content = {
          @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            array = @ArraySchema(schema = @Schema(implementation = PessoaResponseDTO.class))
          )
        }
      ),
      @ApiResponse(description = "There are no people in the database", responseCode = "404",
        content = {
          @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ExceptionResponse.class)
          )
        }
      ),
      @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
      @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
      @ApiResponse(description = "Forbiden", responseCode = "403", content = @Content),
      @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
    }
  )
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
