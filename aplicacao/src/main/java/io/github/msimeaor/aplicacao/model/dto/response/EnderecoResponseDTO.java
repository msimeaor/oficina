package io.github.msimeaor.aplicacao.model.dto.response;

import io.github.msimeaor.aplicacao.enums.UFs;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnderecoResponseDTO extends RepresentationModel<EnderecoResponseDTO> {

  private Long id;
  private String logradouro;
  private UFs uf;

}
