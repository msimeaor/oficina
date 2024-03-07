package io.github.msimeaor.aplicacao.model.utilities.dataPersistence;

public interface PersistenciaDados<O, R> {

  O salvar(O objeto, R repositorio);

}
