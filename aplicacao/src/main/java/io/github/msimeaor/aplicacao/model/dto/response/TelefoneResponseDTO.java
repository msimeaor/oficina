package io.github.msimeaor.aplicacao.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelefoneResponseDTO extends RepresentationModel<TelefoneResponseDTO> {

  private Long id;
  private String numero;

}
