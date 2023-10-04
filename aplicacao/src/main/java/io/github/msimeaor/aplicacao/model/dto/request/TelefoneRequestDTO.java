package io.github.msimeaor.aplicacao.model.dto.request;

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
public class TelefoneRequestDTO {

  @NotBlank(message = "{error.message.notblank}")
  @Size(max = 11, message = "{error.message.size.limit}")
  private String numero;

  @NotNull(message = "{error.message.notnull}")
  private Long pessoa;

}
