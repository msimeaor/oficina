package io.github.msimeaor.aplicacao.model.utilities.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BuscaRegistro<O, R> {

  O buscarPorId(R repository, Long id);
  Page<O> buscarTodosRegistros(R repository, Pageable pageable);
  O buscarPorAtributo(R repository, Object atributo);

}
