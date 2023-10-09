package io.github.msimeaor.aplicacao.model.dto.request;

import io.github.msimeaor.aplicacao.enums.UFs;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnderecoRequestDTO {

  @NotBlank(message = "{error.message.notblank}")
  @Size(max = 100, message = "{error.message.size.limit}")
  private String logradouro;

  @NotNull(message = "{error.message.notnull}")
  private UFs uf;

  private List<Long> pessoasId;

}
