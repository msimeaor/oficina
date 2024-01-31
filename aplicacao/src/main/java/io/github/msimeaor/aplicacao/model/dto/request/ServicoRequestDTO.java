package io.github.msimeaor.aplicacao.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicoRequestDTO {

  @NotBlank(message = "{error.message.notblank}")
  private String nome;

  @NotBlank(message = "{error.message.notblank}")
  private BigDecimal valor;

}
