package io.github.msimeaor.aplicacao.model.utilities.validationClasses;

import io.github.msimeaor.aplicacao.exceptions.geral.EmptyListException;
import io.github.msimeaor.aplicacao.exceptions.veiculo.VeiculoConflictException;
import io.github.msimeaor.aplicacao.model.repository.VeiculoRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class VeiculoValidacao implements ValidacaoDados {

  private String placa;
  private VeiculoRepository repository;

  public VeiculoValidacao() {}

  public void validar() {
    if (repository.findByPlaca(this.placa).isPresent()) {
      throw new VeiculoConflictException("Veiculo já cadastrado!");
    }
  }

  public void setarAtributosEValidarVeiculo(String placa, VeiculoRepository repository) {
    this.placa = placa;
    this.repository = repository;
    validar();
  }

  public void validarLista(Page<?> lista) {
    if (lista.isEmpty()) {
      throw new EmptyListException("Não existem veiculos cadastrados!");
    }
  }

}
