package io.github.msimeaor.aplicacao.model.service.impl;

import io.github.msimeaor.aplicacao.mapper.DozerMapper;
import io.github.msimeaor.aplicacao.model.dto.request.VeiculoRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.VeiculoResponseDTO;
import io.github.msimeaor.aplicacao.model.entity.Veiculo;
import io.github.msimeaor.aplicacao.model.repository.VeiculoRepository;
import io.github.msimeaor.aplicacao.model.service.VeiculoService;
import io.github.msimeaor.aplicacao.model.utilities.dataPersistence.VeiculoPersistencia;
import io.github.msimeaor.aplicacao.model.utilities.validationClasses.VeiculoValidacao;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class VeiculoServiceImpl implements VeiculoService {

  private VeiculoRepository repository;
  private VeiculoValidacao veiculoValidacao;
  private VeiculoPersistencia veiculoPersistencia;

  public VeiculoServiceImpl(VeiculoRepository repository,
                            VeiculoValidacao veiculoValidacao,
                            VeiculoPersistencia veiculoPersistencia) {

    this.repository = repository;
    this.veiculoValidacao = veiculoValidacao;
    this.veiculoPersistencia = veiculoPersistencia;
  }

  public ResponseEntity<VeiculoResponseDTO> save(VeiculoRequestDTO veiculoRequestDTO) {
    veiculoValidacao.setarAtributosEValidarVeiculo(veiculoRequestDTO.getPlaca(), repository);
    Veiculo veiculo = DozerMapper.parseObject(veiculoRequestDTO, Veiculo.class);
    veiculoPersistencia.salvar(veiculo, repository);
    VeiculoResponseDTO veiculoResponseDTO = DozerMapper.parseObject(veiculo, VeiculoResponseDTO.class);

    return new ResponseEntity<>(veiculoResponseDTO, HttpStatus.CREATED);
  }

}
