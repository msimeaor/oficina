package io.github.msimeaor.aplicacao.controller;

import io.github.msimeaor.aplicacao.exceptions.ExceptionResponse;
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

  @Operation(summary = "Save a person in database", description = "Save a person in database",
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
  @PostMapping("/{placa}")
  public ResponseEntity<PessoaResponseDTO> save(
          @RequestBody @Valid PessoaRequestDTO pessoaRequest,
          @PathVariable(name = "placa") String placa
  ) {
    return service.save( pessoaRequest, placa );
  }

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
      @ApiResponse(description = "Person not found in database", responseCode = "404",
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
  @GetMapping("/{id}")
  public ResponseEntity<PessoaResponseDTO> findById( @PathVariable("id") Long id ) {
    return service.findById( id );
  }

  @Operation(summary = "Find all people in database", description = "Find all people in database",
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
      @ApiResponse(description = "There are no people in database", responseCode = "404",
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
  @GetMapping
  public ResponseEntity<PagedModel<EntityModel<PessoaResponseDTO>>> findAll(
          @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
          @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
          @RequestParam(name = "direction", defaultValue = "ASC", required = false) String direction
  ) {

    var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "nome"));
    return service.findAll( pageable );
  }

  @Operation(summary = "Update a person in database", description = "Update a person in database",
    tags = {"Update"},
    responses = {
      @ApiResponse(description = "Success", responseCode = "200",
        content = {
          @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = PessoaResponseDTO.class)
          )
        }
      ),
      @ApiResponse(description = "Resource not found in database", responseCode = "404",
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
  @PutMapping("/{id}")
  public ResponseEntity<PessoaResponseDTO> update( @RequestBody @Valid PessoaRequestDTO pessoaRequest,
                                                   @PathVariable Long id ) {
    return service.update(pessoaRequest, id);
  }

  @Operation(summary = "Find a person in database by part of their name",
    description = "Find a person in database by part of their name",
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
      @ApiResponse(description = "There are no people in database", responseCode = "404",
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
  @GetMapping("/findByNome/{nome}")
  public ResponseEntity<PagedModel<EntityModel<PessoaResponseDTO>>> findByNome(
          @PathVariable(name = "nome") String nome,
          @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
          @RequestParam(name = "size", defaultValue = "5", required = false) Integer size,
          @RequestParam(name = "direction", defaultValue = "ASC", required = false) String direction
  ) {

    var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "nome"));

    return service.findByNomeLike( nome, pageable );
  }

}
