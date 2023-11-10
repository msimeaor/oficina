package io.github.msimeaor.aplicacao.controller;

import io.github.msimeaor.aplicacao.exceptions.ExceptionResponse;
import io.github.msimeaor.aplicacao.model.dto.request.EnderecoRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.EnderecoResponseDTO;
import io.github.msimeaor.aplicacao.model.service.impl.EnderecoServiceImpl;
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
@RequestMapping("/api/enderecos")
@Tag(name = "Endereco Rest Controller")
public class EnderecoRestController {

  private EnderecoServiceImpl service;

  public EnderecoRestController( EnderecoServiceImpl service ) {
      this.service = service;
  }

  @Operation(summary = "Save an address in database", description = "Save an address in database",
    tags = {"Save"},
    responses = {
      @ApiResponse(description = "Success", responseCode = "201",
        content = {
          @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = EnderecoResponseDTO.class)
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
  @PostMapping
  public ResponseEntity<EnderecoResponseDTO> save( @RequestBody @Valid EnderecoRequestDTO enderecoRequest ) {
    return service.save(enderecoRequest);
  }

  @Operation(summary = "Find an address in database by ID", description = "Find an address in database by ID",
    tags = {"Find"},
    responses = {
      @ApiResponse(description = "Success", responseCode = "200",
        content = {
          @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = EnderecoResponseDTO.class)
          )
        }
      ),
      @ApiResponse(description = "Address not found in database", responseCode = "404",
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
  public ResponseEntity<EnderecoResponseDTO> findById( @PathVariable("id") Long id ) {
    return service.findById(id);
  }

  @Operation(summary = "Find all address in database", description = "Find all address in database",
    tags = {"Find"},
    responses = {
      @ApiResponse(description = "Success", responseCode = "200",
        content = {
          @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            array = @ArraySchema(schema = @Schema(implementation = EnderecoResponseDTO.class))
          )
        }
      ),
      @ApiResponse(description = "There are no address in database", responseCode = "404",
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
  public ResponseEntity<PagedModel<EntityModel<EnderecoResponseDTO>>> findAll(
          @RequestParam(name = "page", defaultValue = "0") Integer page,
          @RequestParam(name = "size", defaultValue = "10") Integer size,
          @RequestParam(name = "direction", defaultValue = "ASC") String direction
  ) {

    var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "id"));

    return service.findAll(pageable);
  }

  @Operation(summary = "Update an address in database", description = "Update an address in database",
    tags = {"Update"},
    responses = {
      @ApiResponse(description = "Success", responseCode = "200",
        content = {
          @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = EnderecoResponseDTO.class)
          )
        }
      ),
      @ApiResponse(description = "Street already exists in database", responseCode = "409",
        content = {
          @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ExceptionResponse.class)
          )
        }
      ),
      @ApiResponse(description = "Address not found in database", responseCode = "404",
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
  public ResponseEntity<EnderecoResponseDTO> update( @RequestBody @Valid EnderecoRequestDTO enderecoRequest,
                                                     @PathVariable("id") Long id ) {

    return service.update(enderecoRequest, id);

  }

  @Operation(summary = "Find an address by logradouro", description = "Find an address by logradouro",
    tags = {"Find"},
    responses = {
      @ApiResponse(description = "Success", responseCode = "200",
        content = {
          @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            array = @ArraySchema(schema = @Schema(implementation = EnderecoResponseDTO.class))
          )
        }
      ),
      @ApiResponse(description = "Address Not Found", responseCode = "404",
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
  @GetMapping("/findByLogradouro/{logradouro}")
  public ResponseEntity<PagedModel<EntityModel<EnderecoResponseDTO>>> findByLogradouro(
          @PathVariable(name = "logradouro") String logradouro,
          @RequestParam(name = "page", defaultValue = "0") Integer page,
          @RequestParam(name = "size", defaultValue = "10") Integer size,
          @RequestParam(name = "direction", defaultValue = "ASC") String direction
  ) {

    var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "logradouro"));

    return service.findByLogradouro(logradouro, pageable);
  }

}
