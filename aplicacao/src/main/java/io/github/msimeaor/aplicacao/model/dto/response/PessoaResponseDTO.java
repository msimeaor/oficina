package io.github.msimeaor.aplicacao.model.dto.response;

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
public class PessoaResponseDTO extends RepresentationModel<PessoaResponseDTO> {

  private Long id;
  private String nome;
  private String cpf;
  private LocalDate dataNascimento;
  private String email;
  private EnderecoResponseDTO endereco;
  private List<TelefoneResponseDTO> telefones;
  private boolean inativo;

  /* TODO descomentar essa lista após criar entidade venda
  private List<VendaReponseDTO> vendas;
  */

}
