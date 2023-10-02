package io.github.msimeaor.aplicacao.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;

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
  /*TODO descomentar essas linhas após criar os DTOs correspondentes.
  private EnderecoResponseDTO endereco;
  private List<TelefoneResponseDTO> telefones;
  private List<VendaReponseDTO> vendas;
  */
  private boolean inativo;

}
