package io.github.msimeaor.aplicacao.model.utilities.dataPersistence;

import io.github.msimeaor.aplicacao.model.entity.Veiculo;
import io.github.msimeaor.aplicacao.model.repository.VeiculoRepository;
import org.springframework.stereotype.Component;

@Component
public class VeiculoPersistencia implements PersistenciaDados<Veiculo, VeiculoRepository> {

  public Veiculo salvar(Veiculo objeto, VeiculoRepository repository) {
    return repository.save(objeto);
  }

}
