package io.github.msimeaor.aplicacao.model.utilities.validationClasses;

import org.springframework.data.domain.Page;

public interface ValidacaoDados {

  void validar();
  void validarLista(Page<?> lista);

}
