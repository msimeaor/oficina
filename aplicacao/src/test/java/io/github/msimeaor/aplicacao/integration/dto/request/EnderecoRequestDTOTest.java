package io.github.msimeaor.aplicacao.integration.dto.request;

import io.github.msimeaor.aplicacao.enums.UFs;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnderecoRequestDTOTest {

  private String logradouro;
  private UFs uf;
  private List<Long> pessoasId;

}
