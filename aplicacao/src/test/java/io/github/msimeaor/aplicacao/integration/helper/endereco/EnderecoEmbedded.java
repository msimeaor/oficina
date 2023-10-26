package io.github.msimeaor.aplicacao.integration.helper.endereco;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.msimeaor.aplicacao.integration.dto.response.EnderecoResponseDTOTest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoEmbedded {

  @JsonProperty("enderecoResponseDTOList")
  private List<EnderecoResponseDTOTest> enderecoResponseDTOList;

}
