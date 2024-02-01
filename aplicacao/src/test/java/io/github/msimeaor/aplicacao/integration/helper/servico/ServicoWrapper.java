package io.github.msimeaor.aplicacao.integration.helper.servico;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicoWrapper {

  @JsonProperty("_embedded")
  private ServicoEmbedded servicoEmbedded;

}
