package io.github.msimeaor.aplicacao.integration.helper.pessoa;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.msimeaor.aplicacao.integration.helper.pessoa.PessoaEmbedded;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PessoaWrapper {

  @JsonProperty("_embedded")
  private PessoaEmbedded pessoaEmbedded;

}
