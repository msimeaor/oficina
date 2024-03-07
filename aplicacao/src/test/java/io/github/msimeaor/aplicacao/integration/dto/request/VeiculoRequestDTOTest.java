package io.github.msimeaor.aplicacao.integration.dto.request;

import io.github.msimeaor.aplicacao.enums.Fabricantes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VeiculoRequestDTOTest {

  private String nome;
  private String placa;
  private String kmAtual;
  private String observacao;
  private Fabricantes fabricante;

}
