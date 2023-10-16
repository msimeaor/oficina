package io.github.msimeaor.aplicacao.model.service.impl;

import io.github.msimeaor.aplicacao.controller.PessoaRestController;
import io.github.msimeaor.aplicacao.exceptions.endereco.EnderecoNotFoundException;
import io.github.msimeaor.aplicacao.exceptions.geral.EmptyListException;
import io.github.msimeaor.aplicacao.exceptions.pessoa.PessoaConflictException;
import io.github.msimeaor.aplicacao.exceptions.pessoa.PessoaNotFoundException;
import io.github.msimeaor.aplicacao.mapper.DozerMapper;
import io.github.msimeaor.aplicacao.model.dto.request.PessoaRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.EnderecoResponseDTO;
import io.github.msimeaor.aplicacao.model.dto.response.PessoaResponseDTO;
import io.github.msimeaor.aplicacao.model.dto.response.TelefoneResponseDTO;
import io.github.msimeaor.aplicacao.model.entity.Endereco;
import io.github.msimeaor.aplicacao.model.entity.Pessoa;
import io.github.msimeaor.aplicacao.model.entity.Telefone;
import io.github.msimeaor.aplicacao.model.repository.EnderecoRepository;
import io.github.msimeaor.aplicacao.model.repository.PessoaRepository;
import io.github.msimeaor.aplicacao.model.repository.VeiculoRepository;
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
public class PessoaServiceImpl {

  private PessoaRepository repository;
  private VeiculoRepository veiculoRepository;
  private EnderecoRepository enderecoRepository;
  private PagedResourcesAssembler<PessoaResponseDTO> assembler;

  public PessoaServiceImpl( PessoaRepository repository,
                             VeiculoRepository veiculoRepository,
                             EnderecoRepository enderecoRepository,
                             PagedResourcesAssembler<PessoaResponseDTO> assembler ) {

    this.repository = repository;
    this.veiculoRepository = veiculoRepository;
    this.enderecoRepository = enderecoRepository;
    this.assembler = assembler;
  }


  @Transactional
  public ResponseEntity<PessoaResponseDTO> save( PessoaRequestDTO pessoaRequest, String placa ) {
    validarCadastroExistente(pessoaRequest.getNome(), placa);
    Pessoa pessoa = criarPessoaESalvar(pessoaRequest);
    PessoaResponseDTO pessoaResponseDTO = criarPessoaResponseDTO(pessoa);
    criarLinksHateoasDePessoaResponseDTO(pessoaResponseDTO);

    return new ResponseEntity<>(pessoaResponseDTO, HttpStatus.CREATED);
  }

  protected void validarCadastroExistente(String nome, String placa) {
    if (repository.findByNome(nome).isPresent() && veiculoRepository.findByPlaca(placa).isPresent())
      throw new PessoaConflictException("Cliente já cadastrado!");
  }

  private Pessoa criarPessoaESalvar(PessoaRequestDTO pessoaRequestDTO) {
    Pessoa pessoa = DozerMapper.parseObject(pessoaRequestDTO, Pessoa.class);
    pessoa.setEndereco(buscarEndereco(pessoaRequestDTO.getEnderecoId()));
    return repository.save(pessoa);
  }

  protected Endereco buscarEndereco(Long enderecoId) {
    return enderecoRepository.findById(enderecoId)
            .orElseThrow(() -> new EnderecoNotFoundException("Endereço não encontrado! ID: " + enderecoId));
  }

  private PessoaResponseDTO criarPessoaResponseDTO(Pessoa pessoa) {
    PessoaResponseDTO pessoaResponseDTO = DozerMapper.parseObject(pessoa, PessoaResponseDTO.class);
    pessoaResponseDTO.setTelefonesResponse(converterListaTelefoneEmListaTelefoneResponseDTO(pessoa.getTelefones()));
    pessoaResponseDTO.setEnderecoResponse(converterEnderecoEmEnderecoResponseDTO(pessoa.getEndereco()));
    return pessoaResponseDTO;
  }

  protected List<TelefoneResponseDTO> converterListaTelefoneEmListaTelefoneResponseDTO(List<Telefone> telefones) {
    if (telefones == null)
      return null;

    return telefones.stream().map(telefone ->
            DozerMapper.parseObject(telefone, TelefoneResponseDTO.class))
            .collect(Collectors.toList());
  }

  protected EnderecoResponseDTO converterEnderecoEmEnderecoResponseDTO(Endereco endereco) {
    if (endereco == null)
      return null;

    return DozerMapper.parseObject(endereco, EnderecoResponseDTO.class);
  }

  private void criarLinksHateoasDePessoaResponseDTO(PessoaResponseDTO pessoaResponseDTO) {
    pessoaResponseDTO.add(linkTo(methodOn(PessoaRestController.class)
            .findById(pessoaResponseDTO.getId())).withSelfRel());
  }

  public ResponseEntity<PessoaResponseDTO> findById( Long id ) {
    Pessoa pessoa = buscarPessoa(id);
    PessoaResponseDTO pessoaResponseDTO = criarPessoaResponseDTO(pessoa);
    criarLinksHateoasDePessoaResponseDTO(pessoaResponseDTO);

    return new ResponseEntity<>(pessoaResponseDTO, HttpStatus.OK);
  }

  protected Pessoa buscarPessoa(Long id) {
    return repository.findById(id)
            .orElseThrow(() -> new PessoaNotFoundException("Cliente não encontrado! ID: " + id));
  }

  public ResponseEntity<PagedModel<EntityModel<PessoaResponseDTO>>> findAll( Pageable pageable ) {
    Page<Pessoa> pessoas = criarPagePessoa(pageable);
    Page<PessoaResponseDTO> pessoaResponseDTOS = converterPagePessoaEmPagePessoaResponseDTO(pessoas);
    pessoaResponseDTOS.forEach(this::criarLinksHateoasDePessoaResponseDTO);
    Link link = criarLinkHateoasNavegacaoPorPaginas(pageable);

    return new ResponseEntity<>(assembler.toModel(pessoaResponseDTOS, link), HttpStatus.OK);
  }

  protected Page<Pessoa> criarPagePessoa(Pageable pageable) {
    Page<Pessoa> pessoaPage = repository.findAll(pageable);
    if (pessoaPage.isEmpty())
      throw new EmptyListException("Não existem clientes cadastrados!");

    return pessoaPage;
  }

  protected Page<PessoaResponseDTO> converterPagePessoaEmPagePessoaResponseDTO(Page<Pessoa> pessoaPage) {
    return pessoaPage.map(this::criarPessoaResponseDTO);
  }

  protected Link criarLinkHateoasNavegacaoPorPaginas(Pageable pageable) {
    return linkTo(methodOn(PessoaRestController.class).findAll(
            pageable.getPageNumber(), pageable.getPageSize(), "ASC"
    )).withSelfRel();
  }

  @Transactional
  public ResponseEntity<PessoaResponseDTO> update( PessoaRequestDTO pessoaRequest, Long id ) {
    buscarPessoa(id);
    Pessoa pessoa = atualizarDadosPessoaESalvar(pessoaRequest, id);
    PessoaResponseDTO pessoaResponseDTO = criarPessoaResponseDTO(pessoa);
    criarLinksHateoasDePessoaResponseDTO(pessoaResponseDTO);

    return new ResponseEntity<>(pessoaResponseDTO, HttpStatus.OK);
  }

  private Pessoa atualizarDadosPessoaESalvar(PessoaRequestDTO pessoaRequestDTO, Long id) {
    Pessoa p = DozerMapper.parseObject(pessoaRequestDTO, Pessoa.class);
    p.setId(id);
    p.setEndereco(buscarEndereco(pessoaRequestDTO.getEnderecoId()));
    return repository.save(p);
  }

  public ResponseEntity<PagedModel<EntityModel<PessoaResponseDTO>>> findByNomeLike( String nome, Pageable pageable ) {
    Page<Pessoa> pessoaPage = repository.findByNomeLike("%" + nome + "%", pageable);
    Page<PessoaResponseDTO> pessoaResponseDTOS = converterPagePessoaEmPagePessoaResponseDTO(pessoaPage);
    pessoaResponseDTOS.forEach(this::criarLinksHateoasDePessoaResponseDTO);
    Link link = criarLinkHateoasNavegacaoPorPaginas(pageable);

    return new ResponseEntity<>(assembler.toModel(pessoaResponseDTOS, link), HttpStatus.OK);
  }

  protected Page<Pessoa> criarPagePessoaComFindByNomeLike(String nome, Pageable pageable) {
    Page<Pessoa> pessoaPage = repository.findByNomeLike(nome, pageable);
    if (pessoaPage.isEmpty())
      throw new EmptyListException("Não existem clientes cadastrados!");

    return pessoaPage;
  }

}
