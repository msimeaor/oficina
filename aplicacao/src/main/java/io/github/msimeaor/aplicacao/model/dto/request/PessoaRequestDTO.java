package io.github.msimeaor.aplicacao.model.dto.request;

import io.github.msimeaor.aplicacao.helper.EmailValidationGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PessoaRequestDTO {

  @NotBlank(message = "{error.message.notblank}")
  @Size(max = 50, message = "{error.message.size.limit}")
  private String nome;

  @Email(groups = EmailValidationGroup.class)
  private String email;

  @Size(max = 14, message = "{error.message.invalid.cpf}")
  private String cpf;

  @NotBlank(message = "{error.message.notblank}")
  @Size(max = 9, message = "{error.message.invalid.sexo}")
  private String sexo;

  @Past(message = "{error.message.invalid.date}")
  private LocalDate dataNascimento;

  private List<Long> telefones;
  private Long endereco;

}
