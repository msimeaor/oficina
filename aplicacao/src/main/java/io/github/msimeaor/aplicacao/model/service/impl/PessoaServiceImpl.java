package io.github.msimeaor.aplicacao.model.service.impl;

import io.github.msimeaor.aplicacao.controller.PessoaRestController;
import io.github.msimeaor.aplicacao.exceptions.geral.EmptyListException;
import io.github.msimeaor.aplicacao.exceptions.pessoa.PessoaConflictException;
import io.github.msimeaor.aplicacao.mapper.DozerMapper;
import io.github.msimeaor.aplicacao.model.dto.request.PessoaRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.PessoaResponseDTO;
import io.github.msimeaor.aplicacao.model.entity.Pessoa;
import io.github.msimeaor.aplicacao.model.repository.PessoaRepository;
import io.github.msimeaor.aplicacao.model.repository.VeiculoRepository;
import io.github.msimeaor.aplicacao.model.service.utilities.EnderecoUtilitiesService;
import io.github.msimeaor.aplicacao.model.service.utilities.PessoaUtilitiesService;
import io.github.msimeaor.aplicacao.model.service.utilities.TelefoneUtilitiesService;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PessoaServiceImpl {

  private PessoaRepository repository;
  private PessoaUtilitiesService pessoaUtilitiesService;
  private EnderecoUtilitiesService enderecoUtilitiesService;
  private TelefoneUtilitiesService telefoneUtilitiesService;
  private VeiculoRepository veiculoRepository;
  private PagedResourcesAssembler<PessoaResponseDTO> assembler;

  public PessoaServiceImpl( PessoaRepository repository,
                            PessoaUtilitiesService pessoaUtilitiesService,
                            EnderecoUtilitiesService enderecoUtilitiesService,
                            TelefoneUtilitiesService telefoneUtilitiesService,
                            VeiculoRepository veiculoRepository,
                            PagedResourcesAssembler<PessoaResponseDTO> assembler ) {

    this.repository = repository;
    this.pessoaUtilitiesService = pessoaUtilitiesService;
    this.enderecoUtilitiesService = enderecoUtilitiesService;
    this.telefoneUtilitiesService = telefoneUtilitiesService;
    this.veiculoRepository = veiculoRepository;
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
    if (pessoaRequestDTO.getEnderecoId() != null) {
      pessoa.setEndereco(enderecoUtilitiesService.buscarEndereco(pessoaRequestDTO.getEnderecoId()));
    }
    return repository.save(pessoa);
  }

  private PessoaResponseDTO criarPessoaResponseDTO(Pessoa pessoa) {
    PessoaResponseDTO pessoaResponseDTO = DozerMapper.parseObject(pessoa, PessoaResponseDTO.class);
    pessoaResponseDTO.setTelefonesResponse(
            telefoneUtilitiesService.converterListaTelefoneEmListaTelefoneDTO(pessoa.getTelefones()));
    pessoaResponseDTO.setEnderecoResponse(
            enderecoUtilitiesService.converterEnderecoEmEnderecoResponseDTO(pessoa.getEndereco()));
    return pessoaResponseDTO;
  }

  private void criarLinksHateoasDePessoaResponseDTO(PessoaResponseDTO pessoaResponseDTO) {
    pessoaResponseDTO.add(linkTo(methodOn(PessoaRestController.class)
            .findById(pessoaResponseDTO.getId())).withSelfRel());
  }

  public ResponseEntity<PessoaResponseDTO> findById( Long id ) {
    Pessoa pessoa = pessoaUtilitiesService.buscarPessoa(id);
    PessoaResponseDTO pessoaResponseDTO = criarPessoaResponseDTO(pessoa);
    criarLinksHateoasDePessoaResponseDTO(pessoaResponseDTO);

    return new ResponseEntity<>(pessoaResponseDTO, HttpStatus.OK);
  }

  public ResponseEntity<PagedModel<EntityModel<PessoaResponseDTO>>> findAll( Pageable pageable ) {
    Page<Pessoa> pessoas = criarPagePessoa(pageable);
    Page<PessoaResponseDTO> pessoaResponseDTOS = converterPagePessoaEmPagePessoaResponseDTO(pessoas);
    pessoaResponseDTOS.forEach(this::criarLinksHateoasDePessoaResponseDTO);

    Link link = new HateoasLinkBuilder().gerarLink(PessoaRestController.class, "findAll");

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

  @Transactional
  public ResponseEntity<PessoaResponseDTO> update( PessoaRequestDTO pessoaRequest, Long id ) {
    pessoaUtilitiesService.buscarPessoa(id);
    Pessoa pessoa = atualizarDadosPessoaESalvar(pessoaRequest, id);
    PessoaResponseDTO pessoaResponseDTO = criarPessoaResponseDTO(pessoa);
    criarLinksHateoasDePessoaResponseDTO(pessoaResponseDTO);

    return new ResponseEntity<>(pessoaResponseDTO, HttpStatus.OK);
  }

  private Pessoa atualizarDadosPessoaESalvar(PessoaRequestDTO pessoaRequestDTO, Long id) {
    Pessoa p = DozerMapper.parseObject(pessoaRequestDTO, Pessoa.class);
    p.setId(id);
    if (pessoaRequestDTO.getEnderecoId() != null) {
      p.setEndereco(enderecoUtilitiesService.buscarEndereco(pessoaRequestDTO.getEnderecoId()));
    }
    return repository.save(p);
  }

  public ResponseEntity<PagedModel<EntityModel<PessoaResponseDTO>>> findByNomeLike( String nome, Pageable pageable ) {
    Page<Pessoa> pessoaPage = repository.findByNomeLike("%" + nome + "%", pageable);
    validarPageSize(pessoaPage);
    Page<PessoaResponseDTO> pessoaResponseDTOS = converterPagePessoaEmPagePessoaResponseDTO(pessoaPage);
    pessoaResponseDTOS.forEach(this::criarLinksHateoasDePessoaResponseDTO);

    Link link = new HateoasLinkBuilder().gerarLinkFiltrando(
            PessoaRestController.class, "findByNome", nome);

    return new ResponseEntity<>(assembler.toModel(pessoaResponseDTOS, link), HttpStatus.OK);
  }

  protected void validarPageSize(Page<Pessoa> pessoaPage) {
    if (pessoaPage.isEmpty())
      throw new EmptyListException("Não existem clientes cadastrados que tenham esse nome!");
  }

  protected Page<Pessoa> criarPagePessoaComFindByNomeLike(String nome, Pageable pageable) {
    Page<Pessoa> pessoaPage = repository.findByNomeLike(nome, pageable);
    if (pessoaPage.isEmpty())
      throw new EmptyListException("Não existem clientes cadastrados!");

    return pessoaPage;
  }

}
