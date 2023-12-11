package io.github.msimeaor.aplicacao.model.service.utilities;

import io.github.msimeaor.aplicacao.exceptions.endereco.EnderecoNotFoundException;
import io.github.msimeaor.aplicacao.mapper.DozerMapper;
import io.github.msimeaor.aplicacao.model.dto.response.EnderecoResponseDTO;
import io.github.msimeaor.aplicacao.model.entity.Endereco;
import io.github.msimeaor.aplicacao.model.repository.EnderecoRepository;
import org.springframework.stereotype.Service;

@Service
public class EnderecoUtilitiesService {

  private EnderecoRepository repository;

  public EnderecoUtilitiesService( EnderecoRepository repository ) {
    this.repository = repository;
  }

  public Endereco buscarEndereco(Long id) {
    return repository.findById(id)
            .orElseThrow(() -> new EnderecoNotFoundException("Endereço não encontrado! ID: " + id));
  }

  public EnderecoResponseDTO converterEnderecoEmEnderecoResponseDTO(Endereco endereco) {
    if (endereco == null)
      return null;

    return DozerMapper.parseObject(endereco, EnderecoResponseDTO.class);
  }

}
