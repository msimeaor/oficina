package io.github.msimeaor.aplicacao.model.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

  @NotNull(message = "{error.message.notnull}")
  @Positive(message = "{error.message.negative.value}")
  @Digits(integer = 7, fraction = 2, message = "{error.message.invalid.number}")
  private BigDecimal valor;

}
