package io.github.msimeaor.aplicacao.model.service.impl;

import io.github.msimeaor.aplicacao.exceptions.servico.ServiceConflictException;
import io.github.msimeaor.aplicacao.exceptions.servico.ServicoNotFoundException;
import io.github.msimeaor.aplicacao.mapper.DozerMapper;
import io.github.msimeaor.aplicacao.model.dto.request.ServicoRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.ServicoResponseDTO;
import io.github.msimeaor.aplicacao.model.entity.Servico;
import io.github.msimeaor.aplicacao.model.repository.ServicoRepository;
import io.github.msimeaor.aplicacao.model.service.ServicoService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ServicoServiceImpl implements ServicoService {

  private ServicoRepository repository;

  public ServicoServiceImpl(ServicoRepository repository) {
    this.repository = repository;
  }

  @Transactional
  public ResponseEntity<ServicoResponseDTO> save(ServicoRequestDTO servicoRequestDTO) {
    if (nomeServicoJaCadastrado(servicoRequestDTO.getNome())) {
      throw new ServiceConflictException("Serviço já cadastrado!");
    }

    Servico servico = converterServicoRequestDTOEmServico(servicoRequestDTO);
    servico = salvarServico(servico);
    ServicoResponseDTO servicoResponseDTO = converterServicoEmServicoResponseDTO(servico);

    return new ResponseEntity<>(servicoResponseDTO, HttpStatus.CREATED);
  }

  private boolean nomeServicoJaCadastrado(String serviceName) {
    return repository.findByNome(serviceName).isPresent();
  }

  private Servico converterServicoRequestDTOEmServico(ServicoRequestDTO servicoRequestDTO) {
    return DozerMapper.parseObject(servicoRequestDTO, Servico.class);
  }

  private Servico salvarServico(Servico servico) {
    return repository.save(servico);
  }

  private ServicoResponseDTO converterServicoEmServicoResponseDTO(Servico servico) {
    return DozerMapper.parseObject(servico, ServicoResponseDTO.class);
  }

  public ResponseEntity<ServicoResponseDTO> findById(Long id) {
    Servico servico = buscarServico(id);
    ServicoResponseDTO servicoResponseDTO = converterServicoEmServicoResponseDTO(servico);

    return new ResponseEntity<>(servicoResponseDTO, HttpStatus.OK);
  }

  private Servico buscarServico(Long id) {
    return repository.findById(id)
            .orElseThrow(() -> new ServicoNotFoundException("Serviço não encontrado! ID: " +id));
  }

}
