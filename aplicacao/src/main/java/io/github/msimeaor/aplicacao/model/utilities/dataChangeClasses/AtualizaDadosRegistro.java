package io.github.msimeaor.aplicacao.model.utilities.dataChangeClasses;

public interface AtualizaDadosRegistro<T, R> {

  T atualizarDados(R requestDTO, T objeto);

}
