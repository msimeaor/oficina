package io.github.msimeaor.aplicacao.integration.helper.veiculo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.msimeaor.aplicacao.integration.dto.response.VeiculoResponseDTOTest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VeiculoEmbedded {

  @JsonProperty("veiculoResponseDTOList")
  private List<VeiculoResponseDTOTest> veiculoResponseDTOList;

}
