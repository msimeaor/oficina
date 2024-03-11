package io.github.msimeaor.aplicacao.controller;

import io.github.msimeaor.aplicacao.exceptions.ExceptionResponse;
import io.github.msimeaor.aplicacao.model.dto.request.VeiculoRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.VeiculoResponseDTO;
import io.github.msimeaor.aplicacao.model.service.impl.VeiculoServiceImpl;
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
@RequestMapping("/api/veiculos")
@Tag(name = "Veiculo Rest Controller")
public class VeiculoRestController {

  private VeiculoServiceImpl service;

  public VeiculoRestController(VeiculoServiceImpl service) {
    this.service = service;
  }

  @Operation(summary = "Save a vehicle in database", description = "Save a vehicle in database",
    tags = {"Save"},
    responses = {
      @ApiResponse(description = "Success", responseCode = "201",
        content = {
          @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = VeiculoResponseDTO.class)
          )
        }
      ),
      @ApiResponse(description = "Vehicle with this license plate already registered", responseCode = "409",
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
  public ResponseEntity<VeiculoResponseDTO> save(@RequestBody @Valid VeiculoRequestDTO veiculoRequestDTO) {
    return service.save(veiculoRequestDTO);
  }

  @Operation(summary = "Find a vehicle by ID", description = "Find a vehicle by ID",
    tags = {"Find"},
    responses = {
      @ApiResponse(description = "Success", responseCode = "200",
        content = {
          @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = VeiculoResponseDTO.class)
          )
        }
      ),
      @ApiResponse(description = "Vehicle not found in database", responseCode = "404",
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
  public ResponseEntity<VeiculoResponseDTO> findById(@PathVariable("id") Long id) {
    return service.findById(id);
  }

  @Operation(summary = "Find all records in database", description = "Find all records in database",
    tags = {"Find"},
    responses = {
      @ApiResponse(description = "Success", responseCode = "200",
        content = {
          @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            array = @ArraySchema(schema = @Schema(implementation = VeiculoResponseDTO.class))
          )
        }
      ),
      @ApiResponse(description = "Any vehicles founded in database", responseCode = "404",
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
  public ResponseEntity<PagedModel<EntityModel<VeiculoResponseDTO>>> findAll(
          @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
          @RequestParam(name = "size", defaultValue = "5", required = false) Integer size,
          @RequestParam(name = "direction", defaultValue = "ASC", required = false) String direction
  ) {

    var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "nome"));
    return service.findAll(pageable);
  }

  @Operation(summary = "Find a vehicle by license plate", description = "Find a vehicle by license plate",
    tags = {"Find"},
    responses = {
      @ApiResponse(description = "Success", responseCode = "200",
        content = {
          @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = VeiculoResponseDTO.class)
          )
        }
      ),
      @ApiResponse(description = "Vehicle not found in the databas", responseCode = "404",
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
  @GetMapping("/findByPlaca/{placa}")
  public ResponseEntity<VeiculoResponseDTO> findByPlaca(@PathVariable("placa") String placa) {
    return service.findByPlaca(placa);
  }

  @PutMapping("{id}")
  public ResponseEntity<VeiculoResponseDTO> update(@RequestBody @Valid VeiculoRequestDTO veiculoRequestDTO,
                                                   @PathVariable("id") Long id) {

    return service.update(veiculoRequestDTO, id);
  }

}
