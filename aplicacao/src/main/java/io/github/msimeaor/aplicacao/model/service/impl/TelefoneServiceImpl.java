package io.github.msimeaor.aplicacao.model.service.impl;

import io.github.msimeaor.aplicacao.controller.PessoaRestController;
import io.github.msimeaor.aplicacao.controller.TelefoneRestController;
import io.github.msimeaor.aplicacao.exceptions.geral.EmptyListException;
import io.github.msimeaor.aplicacao.exceptions.telefone.TelefoneConflictException;
import io.github.msimeaor.aplicacao.exceptions.telefone.TelefoneNotFoundException;
import io.github.msimeaor.aplicacao.mapper.DozerMapper;
import io.github.msimeaor.aplicacao.model.dto.request.TelefoneRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.TelefoneResponseDTO;
import io.github.msimeaor.aplicacao.model.entity.Pessoa;
import io.github.msimeaor.aplicacao.model.entity.Telefone;
import io.github.msimeaor.aplicacao.model.repository.TelefoneRepository;
import io.github.msimeaor.aplicacao.model.service.TelefoneService;
import io.github.msimeaor.aplicacao.model.service.utilities.PessoaUtilitiesService;
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
public class TelefoneServiceImpl implements TelefoneService {

  private TelefoneRepository repository;
  private PessoaUtilitiesService pessoaUtilitiesService;
  private PagedResourcesAssembler<TelefoneResponseDTO> assembler;

  public TelefoneServiceImpl(TelefoneRepository repository,
                             PessoaUtilitiesService pessoaUtilitiesService,
                             PagedResourcesAssembler<TelefoneResponseDTO> assembler) {

    this.repository = repository;
    this.pessoaUtilitiesService = pessoaUtilitiesService;
    this.assembler = assembler;
  }

  @Transactional
  public ResponseEntity<TelefoneResponseDTO> save( TelefoneRequestDTO telefoneRequest ) {
    validarNumero(telefoneRequest.getNumero());
    Pessoa pessoa = pessoaUtilitiesService.buscarPessoa(telefoneRequest.getPessoaId());
    Telefone telefone = criarTelefoneESalvar(telefoneRequest, pessoa);
    pessoaUtilitiesService.AdicionarNovoTelefoneParaPessoa(pessoa, telefone);
    TelefoneResponseDTO telefoneResponseDTO = criarTelefoneResponseDTO(telefone);
    criarLinksHateoasSelfRelEProprietario(telefoneResponseDTO, telefone);

    return new ResponseEntity<>(telefoneResponseDTO, HttpStatus.CREATED);
  }

  private void validarNumero(String numero) {
    if (repository.findByNumero(numero) != null)
      throw new TelefoneConflictException("Numero já cadastrado!");
  }

  private Telefone criarTelefoneESalvar(TelefoneRequestDTO telefoneRequestDTO, Pessoa pessoa) {
    Telefone telefone = DozerMapper.parseObject(telefoneRequestDTO, Telefone.class);
    telefone.setPessoa(pessoa);
    return repository.save(telefone);
  }

  private TelefoneResponseDTO criarTelefoneResponseDTO(Telefone telefone) {
    return DozerMapper.parseObject(telefone, TelefoneResponseDTO.class);
  }

  private void criarLinksHateoasSelfRelEProprietario(TelefoneResponseDTO telefoneResponse, Telefone telefone) {
    criarLinkHateoasSelfrel(telefoneResponse);
    criarLinkHateoasProprietario(telefoneResponse, telefone);
  }

  private void criarLinkHateoasSelfrel(TelefoneResponseDTO telefoneResponseDTO) {
    telefoneResponseDTO.add(linkTo(methodOn(TelefoneRestController.class)
            .findById(telefoneResponseDTO.getId())).withSelfRel());
  }

  private void criarLinkHateoasProprietario(TelefoneResponseDTO telefoneResponseDTO, Telefone telefone) {
    telefoneResponseDTO.add(linkTo(methodOn(PessoaRestController.class)
            .findById(telefone.getPessoa().getId())).withRel("Proprietário"));
  }

  public ResponseEntity<TelefoneResponseDTO> findById( Long id ) {
    Telefone telefone = buscarTelefone(id);
    TelefoneResponseDTO telefoneResponseDTO = criarTelefoneResponseDTO(telefone);
    criarLinksHateoasSelfRelEProprietario(telefoneResponseDTO, telefone);

    return new ResponseEntity<>(telefoneResponseDTO, HttpStatus.OK);
  }

  private Telefone buscarTelefone(Long id) {
    return repository.findById(id)
            .orElseThrow(() -> new TelefoneNotFoundException("Telefone não encontrado! ID: " + id));
  }

  public ResponseEntity<PagedModel<EntityModel<TelefoneResponseDTO>>> findAll( Pageable pageable ) {
    Page<Telefone> telefonePage = criarPageTelefone(pageable);
    Page<TelefoneResponseDTO> telefoneResponseDTOS = criarPageTelefoneResponseDTO(telefonePage);
    iterarTelefoneResponseDTOECriarLinksHateoas(telefoneResponseDTOS, telefonePage);

    Link link = new HateoasLinkBuilder().gerarLink(TelefoneRestController.class, "findAll");

    return new ResponseEntity<>(assembler.toModel(telefoneResponseDTOS, link), HttpStatus.OK);
  }

  private Page<Telefone> criarPageTelefone(Pageable pageable) {
    Page<Telefone> telefonePage = repository.findAll(pageable);
    if (telefonePage.isEmpty())
      throw new EmptyListException("Não existem telefones cadastrados!");

    return telefonePage;
  }

  private Page<TelefoneResponseDTO> criarPageTelefoneResponseDTO(Page<Telefone> telefonePage) {
    return telefonePage.map(
            telefone -> DozerMapper.parseObject(telefone, TelefoneResponseDTO.class)
    );
  }

  private void iterarTelefoneResponseDTOECriarLinksHateoas(Page<TelefoneResponseDTO> telefoneResponseDTOS,
                                                             Page<Telefone> telefonePage) {

    telefoneResponseDTOS.forEach(telefoneResponse -> {
      for (Telefone telefone : telefonePage) {
        if (telefoneResponse.getId().equals(telefone.getId())) {
          criarLinksHateoasSelfRelEProprietario(telefoneResponse, telefone);
        }
      }
    });
  }

  @Transactional
  public ResponseEntity<TelefoneResponseDTO> update( TelefoneRequestDTO telefoneRequest, Long id ) {
    validarNumero(telefoneRequest.getNumero());
    Pessoa pessoa = pessoaUtilitiesService.buscarPessoa(telefoneRequest.getPessoaId());
    buscarTelefone(id);
    Telefone telefone = atualizarDadosTelefone(telefoneRequest, pessoa, id);
    TelefoneResponseDTO telefoneResponseDTO = criarTelefoneResponseDTO(telefone);
    criarLinksHateoasSelfRelEProprietario(telefoneResponseDTO, telefone);

    return new ResponseEntity<>(telefoneResponseDTO, HttpStatus.OK);
  }

  private Telefone atualizarDadosTelefone(TelefoneRequestDTO telefoneRequestDTO, Pessoa pessoa, Long id) {
    Telefone telefone = DozerMapper.parseObject(telefoneRequestDTO, Telefone.class);
    telefone.setId(id);
    telefone.setPessoa(pessoa);
    return repository.save(telefone);
  }

  public ResponseEntity<TelefoneResponseDTO> findByNumero(String numero) {
    Telefone telefone = buscarTelefonePorNumero(numero);
    TelefoneResponseDTO telefoneResponseDTO = criarTelefoneResponseDTO(telefone);
    criarLinksHateoasSelfRelEProprietario(telefoneResponseDTO, telefone);

    return new ResponseEntity<>(telefoneResponseDTO, HttpStatus.OK);
  }

  private Telefone buscarTelefonePorNumero(String numero) {
    Telefone telefone = repository.findByNumero(numero);
    if (telefone == null)
      throw new TelefoneNotFoundException("Telefone não encontrado! Numero: " + numero);

    return telefone;
  }

}
