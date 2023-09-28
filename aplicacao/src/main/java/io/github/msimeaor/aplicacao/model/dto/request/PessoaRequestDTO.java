package io.github.msimeaor.aplicacao.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PessoaRequestDTO {

  private String nome;
  private String email;
  private String cpf;
  private String sexo;
  private List<Long> telefones;
  private Long endereco;

}
