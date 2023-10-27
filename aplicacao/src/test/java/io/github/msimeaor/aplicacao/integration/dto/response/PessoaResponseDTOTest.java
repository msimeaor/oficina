package io.github.msimeaor.aplicacao.integration.dto.response;

import io.github.msimeaor.aplicacao.model.dto.response.EnderecoResponseDTO;
import io.github.msimeaor.aplicacao.model.dto.response.TelefoneResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PessoaResponseDTOTest extends RepresentationModel<PessoaResponseDTOTest> {

  private Long id;
  private String nome;
  private String cpf;
  private LocalDate dataNascimento;
  private String email;
  private String sexo;
  private EnderecoResponseDTO enderecoResponse;
  private List<TelefoneResponseDTO> telefonesResponse;
  private boolean inativo;

  /* TODO descomentar essa lista após criar entidade venda
  private List<VendaReponseDTO> vendas;
  */

}
