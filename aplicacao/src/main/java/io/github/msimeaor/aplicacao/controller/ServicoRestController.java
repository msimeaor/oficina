package io.github.msimeaor.aplicacao.controller;

import io.github.msimeaor.aplicacao.exceptions.ExceptionResponse;
import io.github.msimeaor.aplicacao.model.dto.request.ServicoRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.ServicoResponseDTO;
import io.github.msimeaor.aplicacao.model.service.impl.ServicoServiceImpl;
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
@RequestMapping("api/servicos")
@Tag(name = "Serviço Rest Controller")
public class ServicoRestController {

  private ServicoServiceImpl servicoService;

  public ServicoRestController(ServicoServiceImpl servicoService) {
    this.servicoService = servicoService;
  }

  @Operation(summary = "save a service in database", description = "save a service in database",
    tags = {"Save"},
    responses = {
      @ApiResponse(description = "Success", responseCode = "201",
        content = {
          @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ServicoResponseDTO.class)
          )
        }
      ),
      @ApiResponse(description = "Service with a name already registered in database", responseCode = "409",
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
  @PostMapping()
  public ResponseEntity<ServicoResponseDTO> save(@Valid @RequestBody ServicoRequestDTO servicoRequestDTO) {
    return servicoService.save(servicoRequestDTO);
  }

  @Operation(summary = "Searching a service by ID", description = "Searching a service by ID",
    tags = {"Find"},
    responses = {
      @ApiResponse(description = "Success", responseCode = "200",
        content = {
          @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ServicoResponseDTO.class)
          )
        }
      ),
      @ApiResponse(description = "Service not found in database", responseCode = "404",
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
  public ResponseEntity<ServicoResponseDTO> findById(@PathVariable("id") Long id) {
    return servicoService.findById(id);
  }

  @Operation(summary = "Searching a service by name", description = "Searching a service by name",
    tags = {"Find"},
    responses = {
      @ApiResponse(description = "Success", responseCode = "200",
        content = {
          @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            array = @ArraySchema(schema = @Schema(implementation = ServicoResponseDTO.class))
          )
        }
      ),
      @ApiResponse(description = "no services found in database", responseCode = "404",
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
  public ResponseEntity<PagedModel<EntityModel<ServicoResponseDTO>>> findByNome(
          @PathVariable("nome") String nome,
          @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
          @RequestParam(name = "size", defaultValue = "5", required = false) Integer size,
          @RequestParam(name = "direction", defaultValue = "ASC", required = false) String direction
  ) {

    var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "nome"));

    return servicoService.findByNome(nome, pageable);
  }

  @Operation(summary = "Searching for all services", description = "Searching for all services",
    tags = {"Find"},
    responses = {
      @ApiResponse(description = "Success", responseCode = "200",
        content = {
          @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            array = @ArraySchema(schema = @Schema(implementation = ServicoResponseDTO.class))
          )
        }
      ),
      @ApiResponse(description = "no services found in database", responseCode = "404",
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
  @GetMapping()
  public ResponseEntity<PagedModel<EntityModel<ServicoResponseDTO>>> findAll(
          @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
          @RequestParam(name = "size", defaultValue = "5", required = false) Integer size,
          @RequestParam(name = "direction", defaultValue = "ASC", required = false) String direction
  ) {

    var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "nome"));

    return servicoService.findAll(pageable);
  }

  @Operation(summary = "Updating a service", description = "Updating a service",
    tags = {"Update"},
    responses = {
      @ApiResponse(description = "Success", responseCode = "200",
        content = {
          @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ServicoResponseDTO.class)
          )
        }
      ),
      @ApiResponse(description = "Service with this ID not found", responseCode = "404",
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
  public ResponseEntity<ServicoResponseDTO> update(@RequestBody @Valid ServicoRequestDTO servicoRequestDTO,
                                                   @PathVariable("id") Long id) {

    return servicoService.update(servicoRequestDTO, id);
  }

}
