package io.github.msimeaor.aplicacao.integration.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelefoneResponseDTOTest extends RepresentationModel<TelefoneResponseDTOTest> {

  private Long id;
  private String numero;

}
