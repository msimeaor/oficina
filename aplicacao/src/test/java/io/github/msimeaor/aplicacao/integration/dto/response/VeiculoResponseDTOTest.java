package io.github.msimeaor.aplicacao.integration.dto.response;

import io.github.msimeaor.aplicacao.enums.Fabricantes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VeiculoResponseDTOTest {

  private Long id;
  private String nome;
  private String placa;
  private String kmAtual;
  private String observacao;
  private Fabricantes fabricante;

}
