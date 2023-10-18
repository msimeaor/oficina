package io.github.msimeaor.aplicacao.model.service.impl;

import io.github.msimeaor.aplicacao.controller.EnderecoRestController;
import io.github.msimeaor.aplicacao.controller.PessoaRestController;
import io.github.msimeaor.aplicacao.exceptions.endereco.EnderecoConflictException;
import io.github.msimeaor.aplicacao.exceptions.endereco.EnderecoNotFoundException;
import io.github.msimeaor.aplicacao.exceptions.geral.EmptyListException;
import io.github.msimeaor.aplicacao.exceptions.pessoa.PessoaNotFoundException;
import io.github.msimeaor.aplicacao.mapper.DozerMapper;
import io.github.msimeaor.aplicacao.model.dto.request.EnderecoRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.EnderecoResponseDTO;
import io.github.msimeaor.aplicacao.model.entity.Endereco;
import io.github.msimeaor.aplicacao.model.entity.Pessoa;
import io.github.msimeaor.aplicacao.model.repository.EnderecoRepository;
import io.github.msimeaor.aplicacao.model.repository.PessoaRepository;
import io.github.msimeaor.aplicacao.model.service.EnderecoService;
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

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class EnderecoServiceImpl implements EnderecoService {

  private EnderecoRepository repository;
  private PessoaRepository pessoaRepository;
  private PagedResourcesAssembler<EnderecoResponseDTO> assembler;

  public EnderecoServiceImpl( EnderecoRepository repository,
                              PessoaRepository pessoaRepository,
                              PagedResourcesAssembler<EnderecoResponseDTO> assembler) {
    this.repository = repository;
    this.pessoaRepository = pessoaRepository;
    this.assembler = assembler;
  }

  @Transactional
  public ResponseEntity<EnderecoResponseDTO> save( EnderecoRequestDTO enderecoRequest ) {
    validarLogradouro(enderecoRequest.getLogradouro());
    List<Pessoa> pessoas = criarListaPessoaPorId(enderecoRequest.getPessoasId());
    Endereco endereco = criarEnderecoESalvar(enderecoRequest, pessoas);
    EnderecoResponseDTO enderecoResponseDTO = criarEnderecoResponse(endereco);
    criarLinksHateoasDeEnderecoResponseDTO(enderecoResponseDTO, pessoas);
    atualizarPessoaRelacionandoEndereco(pessoas, endereco);

    return new ResponseEntity<>(enderecoResponseDTO, HttpStatus.CREATED);
  }

  protected void validarLogradouro(String logradouro) {
    if (repository.findByLogradouro(logradouro) != null)
      throw new EnderecoConflictException("Logradouro já cadastrado!");
  }

  protected List<Pessoa> criarListaPessoaPorId(List<Long> pessoasId) {
    if (pessoasId == null)
      return null;

    return pessoasId.stream().map((pessoaId) -> pessoaRepository.findById(pessoaId)
            .orElseThrow(() -> new PessoaNotFoundException("Cliente não encontrado! ID: " + pessoaId)))
            .collect(Collectors.toList());
  }

  protected Endereco criarEnderecoESalvar(EnderecoRequestDTO enderecoRequestDTO, List<Pessoa> pessoas) {
    Endereco endereco = DozerMapper.parseObject(enderecoRequestDTO, Endereco.class);
    endereco.setPessoas(pessoas);
    return repository.save(endereco);
  }

  protected EnderecoResponseDTO criarEnderecoResponse(Endereco endereco) {
    return DozerMapper.parseObject(endereco, EnderecoResponseDTO.class);
  }

  private void criarLinksHateoasDeEnderecoResponseDTO(EnderecoResponseDTO enderecoResponseDTO, List<Pessoa> pessoas) {
    criarLinkHateoasMoradores(enderecoResponseDTO, pessoas);
    criarLinkHateoasSelfRel(enderecoResponseDTO);
  }

  protected void criarLinkHateoasMoradores(EnderecoResponseDTO enderecoResponseDTO, List<Pessoa> pessoas) {
    if (pessoas != null) {
      pessoas.forEach(
              pessoa -> enderecoResponseDTO.add(linkTo(methodOn(PessoaRestController.class)
                      .findById(pessoa.getId())).withRel("Morador(es)"))
      );
    }
  }

  private void criarLinkHateoasSelfRel(EnderecoResponseDTO enderecoResponseDTO) {
    enderecoResponseDTO.add(linkTo(methodOn(EnderecoRestController.class)
            .findById(enderecoResponseDTO.getId())).withSelfRel());
  }

  protected void atualizarPessoaRelacionandoEndereco(List<Pessoa> pessoas, Endereco endereco) {
    if (pessoas != null) {
      pessoas.forEach(
              pessoa -> pessoa.setEndereco(endereco)
      );
    }
  }

  public ResponseEntity<EnderecoResponseDTO> findById( Long id ) {
    Endereco endereco = buscarEndereco(id);
    EnderecoResponseDTO enderecoResponseDTO = criarEnderecoResponse(endereco);
    criarLinksHateoasDeEnderecoResponseDTO(enderecoResponseDTO, endereco.getPessoas());

    return new ResponseEntity<>(enderecoResponseDTO, HttpStatus.OK);
  }

  protected Endereco buscarEndereco(Long id) {
    return repository.findById(id)
            .orElseThrow(() -> new EnderecoNotFoundException("Endereço não encontrado! ID: " + id));
  }

  public ResponseEntity<PagedModel<EntityModel<EnderecoResponseDTO>>> findAll( Pageable pageable ) {
    Page<Endereco> enderecoPage = criarPageEndereco(pageable);
    Page<EnderecoResponseDTO> enderecoResponseDTOS = converterPageEnderecoEmPageEnderecoResponseDTO(enderecoPage);
    criarLinksHateoasPageEnderecoResponseDTO(enderecoResponseDTOS, enderecoPage);
    Link link = criarLinkHateoasNavegacaoEntrePaginas(pageable);

    return new ResponseEntity<>(assembler.toModel(enderecoResponseDTOS, link), HttpStatus.OK);
  }

  protected Page<Endereco> criarPageEndereco(Pageable pageable) {
    Page<Endereco> enderecoPage = repository.findAll(pageable);
    if (enderecoPage.isEmpty())
      throw new EmptyListException("Não existem endereços cadastrados!");

    return enderecoPage;
  }

  protected Page<EnderecoResponseDTO> converterPageEnderecoEmPageEnderecoResponseDTO(Page<Endereco> enderecoPage) {
    return enderecoPage.map(
            endereco -> DozerMapper.parseObject(endereco, EnderecoResponseDTO.class)
    );
  }

  protected void criarLinksHateoasPageEnderecoResponseDTO(Page<EnderecoResponseDTO> enderecoResponseDTOS,
                                                        Page<Endereco> enderecoPage) {

    enderecoResponseDTOS.forEach(enderecoResponse -> enderecoPage.forEach(endereco -> {
      if (enderecoResponse.getId().equals(endereco.getId())) {
        criarLinksHateoasDeEnderecoResponseDTO(enderecoResponse, endereco.getPessoas());
      }
    }));
  }

  protected Link criarLinkHateoasNavegacaoEntrePaginas(Pageable pageable) {
    return linkTo(methodOn(EnderecoRestController.class).findAll(
            pageable.getPageNumber(), pageable.getPageSize(), "ASC"
    )).withSelfRel();
  }

  @Transactional
  public ResponseEntity<EnderecoResponseDTO> update( EnderecoRequestDTO enderecoRequest, Long id ) {
    validarLogradouro(enderecoRequest.getLogradouro());
    Endereco endereco = buscarEndereco(id);
    endereco = atualizarEnderecoESalvar(enderecoRequest, endereco.getPessoas(), id);
    EnderecoResponseDTO enderecoResponse = criarEnderecoResponse(endereco);
    criarLinksHateoasDeEnderecoResponseDTO(enderecoResponse, endereco.getPessoas());

    return new ResponseEntity<>(enderecoResponse, HttpStatus.OK);
  }

  protected Endereco atualizarEnderecoESalvar(EnderecoRequestDTO enderecoRequestDTO, List<Pessoa> pessoas , Long id) {
    Endereco endereco = DozerMapper.parseObject(enderecoRequestDTO, Endereco.class);
    endereco.setId(id);
    endereco.setPessoas(pessoas);
    return repository.save(endereco);
  }

}
