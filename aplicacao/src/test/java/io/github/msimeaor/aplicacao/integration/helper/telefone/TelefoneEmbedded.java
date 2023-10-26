package io.github.msimeaor.aplicacao.integration.helper.telefone;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.msimeaor.aplicacao.integration.dto.response.TelefoneResponseDTOTest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelefoneEmbedded {

  @JsonProperty("telefoneResponseDTOList")
  private List<TelefoneResponseDTOTest> telefoneResponseDTOList;

}
