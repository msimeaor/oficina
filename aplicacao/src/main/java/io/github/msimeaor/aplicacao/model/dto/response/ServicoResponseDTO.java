package io.github.msimeaor.aplicacao.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicoResponseDTO extends RepresentationModel<ServicoResponseDTO> {

  private Long id;
  private String nome;
  private BigDecimal valor;

}
