package io.github.msimeaor.aplicacao.integration.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PessoaRequestDTOTest {

  private String nome;
  private String email;
  private String cpf;
  private String sexo;
  private LocalDate dataNascimento;
  private Long enderecoId;

}
