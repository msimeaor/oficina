package io.github.msimeaor.aplicacao.integration.helper.veiculo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VeiculoWrapper {

  @JsonProperty("_embedded")
  private VeiculoEmbedded embedded;

}
