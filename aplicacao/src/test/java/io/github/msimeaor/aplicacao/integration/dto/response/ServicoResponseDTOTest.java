package io.github.msimeaor.aplicacao.integration.dto.response;

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
public class ServicoResponseDTOTest extends RepresentationModel<ServicoResponseDTOTest> {

  private Long id;
  private String nome;
  private BigDecimal valor;

}
