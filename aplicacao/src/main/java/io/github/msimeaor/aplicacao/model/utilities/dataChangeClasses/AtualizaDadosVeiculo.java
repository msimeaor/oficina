package io.github.msimeaor.aplicacao.model.utilities.dataChangeClasses;

import io.github.msimeaor.aplicacao.model.dto.request.VeiculoRequestDTO;
import io.github.msimeaor.aplicacao.model.entity.Veiculo;
import org.springframework.stereotype.Component;

@Component
public class AtualizaDadosVeiculo implements AtualizaDadosRegistro<Veiculo, VeiculoRequestDTO> {

  public Veiculo atualizarDados(VeiculoRequestDTO veiculoRequestDTO, Veiculo veiculo) {
    veiculo.setNome(veiculoRequestDTO.getNome());
    veiculo.setPlaca(veiculoRequestDTO.getPlaca());
    veiculo.setKmAtual(veiculoRequestDTO.getKmAtual());
    veiculo.setFabricante(veiculoRequestDTO.getFabricante());
    veiculo.setObservacao(veiculoRequestDTO.getObservacao());

    return veiculo;
  }

}
