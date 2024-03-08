package io.github.msimeaor.aplicacao.model.utilities.search;

import io.github.msimeaor.aplicacao.exceptions.veiculo.VeiculoNotFoundException;
import io.github.msimeaor.aplicacao.model.entity.Veiculo;
import io.github.msimeaor.aplicacao.model.repository.VeiculoRepository;
import org.springframework.stereotype.Component;

@Component
public class BuscaVeiculo implements BuscaRegistro<Veiculo, VeiculoRepository> {

  public BuscaVeiculo() {}

  public Veiculo buscarPorId(VeiculoRepository repository, Long id) {
    return repository.findById(id)
            .orElseThrow(() -> new VeiculoNotFoundException("Veiculo não encontrado! ID: " + id));
  }

}