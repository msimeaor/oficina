package io.github.msimeaor.aplicacao.model.service.impl;

import io.github.msimeaor.aplicacao.controller.ServicoRestController;
import io.github.msimeaor.aplicacao.exceptions.geral.EmptyListException;
import io.github.msimeaor.aplicacao.exceptions.servico.ServiceConflictException;
import io.github.msimeaor.aplicacao.exceptions.servico.ServicoNotFoundException;
import io.github.msimeaor.aplicacao.mapper.DozerMapper;
import io.github.msimeaor.aplicacao.model.dto.request.ServicoRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.ServicoResponseDTO;
import io.github.msimeaor.aplicacao.model.entity.Servico;
import io.github.msimeaor.aplicacao.model.repository.ServicoRepository;
import io.github.msimeaor.aplicacao.model.service.ServicoService;
import io.github.msimeaor.aplicacao.model.utilities.HateoasLinkBuilder;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ServicoServiceImpl implements ServicoService {

  private ServicoRepository repository;
  private PagedResourcesAssembler<ServicoResponseDTO> assembler;

  public ServicoServiceImpl(ServicoRepository repository,
                            PagedResourcesAssembler<ServicoResponseDTO> assembler) {

    this.repository = repository;
    this.assembler = assembler;
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

  public ResponseEntity<PagedModel<EntityModel<ServicoResponseDTO>>> findByNome(String nome, Pageable pageable) {
    Page<Servico> servicoPage = criarPageServico(nome, pageable);
    validarPageServico(servicoPage);
    Page<ServicoResponseDTO> servicoResponseDTOPage = converterPageServicoEmPageServicoResponseDTO(servicoPage);

    Link link = new HateoasLinkBuilder()
            .gerarLinkFiltrando(ServicoRestController.class, "findByNome", nome);

    return new ResponseEntity<>(assembler.toModel(servicoResponseDTOPage, link), HttpStatus.OK);
  }

  private Page<Servico> criarPageServico(String nome, Pageable pageable) {
    if (nome != null) {
      nome = "%" + nome + "%";
      return repository.findByNome(nome, pageable);
    }

    return repository.findAll(pageable);
  }

  private void validarPageServico(Page<Servico> servicoPage) {
    if (servicoPage.isEmpty()) {
      throw new EmptyListException("Não existem serviços cadastrados!");
    }
  }

  private Page<ServicoResponseDTO> converterPageServicoEmPageServicoResponseDTO(Page<Servico> servicoPage) {
    return servicoPage.map(this::converterServicoEmServicoResponseDTO);
  }

  public ResponseEntity<PagedModel<EntityModel<ServicoResponseDTO>>> findAll(Pageable pageable) {
    Page<Servico> servicoPage = criarPageServico(null, pageable);
    validarPageServico(servicoPage);
    Page<ServicoResponseDTO> servicoResponseDTOPage = converterPageServicoEmPageServicoResponseDTO(servicoPage);

    Link link = new HateoasLinkBuilder().gerarLink(ServicoRestController.class, "findAll");

    return new ResponseEntity<>(assembler.toModel(servicoResponseDTOPage, link), HttpStatus.OK);
  }

  @Transactional
  public ResponseEntity<ServicoResponseDTO> update(ServicoRequestDTO servicoRequestDTO, Long id) {
    Servico servico = buscarServico(id);
    servico = substituirValorDoServicoESalvar(servico, servicoRequestDTO);
    ServicoResponseDTO servicoResponseDTO = converterServicoEmServicoResponseDTO(servico);

    return new ResponseEntity<>(servicoResponseDTO, HttpStatus.OK);
  }

  private Servico substituirValorDoServicoESalvar(Servico servico, ServicoRequestDTO servicoRequestDTO) {
    servico.setValor(servicoRequestDTO.getValor());
    return repository.save(servico);
  }

}
