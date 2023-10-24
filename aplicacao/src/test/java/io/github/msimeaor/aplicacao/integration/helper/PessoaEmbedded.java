package io.github.msimeaor.aplicacao.integration.helper;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.msimeaor.aplicacao.integration.dto.response.PessoaResponseDTOTest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PessoaEmbedded {

  @JsonProperty("pessoaResponseDTOList")
  private List<PessoaResponseDTOTest> pessoaResponseDTOList;

}
