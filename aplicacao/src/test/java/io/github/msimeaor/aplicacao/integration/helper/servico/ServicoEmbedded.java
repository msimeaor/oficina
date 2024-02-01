package io.github.msimeaor.aplicacao.integration.helper.servico;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.msimeaor.aplicacao.integration.dto.response.ServicoResponseDTOTest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicoEmbedded {

  @JsonProperty("servicoResponseDTOList")
  private List<ServicoResponseDTOTest> servicoResponseDTOList;

}
