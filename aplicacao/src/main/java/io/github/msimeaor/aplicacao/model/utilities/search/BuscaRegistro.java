package io.github.msimeaor.aplicacao.model.utilities.search;

public interface BuscaRegistro<O, R> {

  O buscarPorId(R repository, Long id);

}
