package io.github.msimeaor.aplicacao.integration.helper.endereco;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoWrapper {

  @JsonProperty("_embedded")
  private EnderecoEmbedded enderecoEmbedded;

}
