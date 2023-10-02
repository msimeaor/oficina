package io.github.msimeaor.aplicacao.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExceptionResponse {

  private Integer codigoStatus;
  private String mensagemErro;
  private String detalhesErro;
  private Date timeStamp;

}
