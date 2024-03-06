package io.github.msimeaor.aplicacao.model.dto.request;

import io.github.msimeaor.aplicacao.enums.Fabricantes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VeiculoRequestDTO {

  @NotBlank(message = "{error.message.notblank}")
  @Size(max = 100, message = "{error.message.size.limit}")
  private String nome;

  @NotBlank(message = "{error.message.notblank}")
  @Size(max = 7, message = "{error.message.size.limit}")
  private String placa;

  @Size(max = 7, message = "{error.message.size.limit}")
  private String kmAtual;

  @Size(max = 255, message = "{error.message.size.limit}")
  private String observacao;

  @NotNull(message = "{error.message.notnull}")
  private Fabricantes fabricante;

}
