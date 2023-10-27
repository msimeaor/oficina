package io.github.msimeaor.aplicacao.integration.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelefoneRequestDTOTest {

  private String numero;
  private Long pessoaId;

}
