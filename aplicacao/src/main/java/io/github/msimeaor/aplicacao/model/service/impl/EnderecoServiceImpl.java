package io.github.msimeaor.aplicacao.model.service.impl;

import io.github.msimeaor.aplicacao.controller.EnderecoRestController;
import io.github.msimeaor.aplicacao.controller.PessoaRestController;
import io.github.msimeaor.aplicacao.exceptions.endereco.EnderecoConflictException;
import io.github.msimeaor.aplicacao.exceptions.geral.EmptyListException;
import io.github.msimeaor.aplicacao.mapper.DozerMapper;
import io.github.msimeaor.aplicacao.model.dto.request.EnderecoRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.EnderecoResponseDTO;
import io.github.msimeaor.aplicacao.model.entity.Endereco;
import io.github.msimeaor.aplicacao.model.entity.Pessoa;
import io.github.msimeaor.aplicacao.model.repository.EnderecoRepository;
import io.github.msimeaor.aplicacao.model.service.EnderecoService;
import io.github.msimeaor.aplicacao.model.service.utilities.EnderecoUtilitiesService;
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

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class EnderecoServiceImpl implements EnderecoService {

  private EnderecoRepository repository;
  private EnderecoUtilitiesService enderecoUtilitiesService;
  private PessoaUtilitiesService pessoaUtilitiesService;
  private PagedResourcesAssembler<EnderecoResponseDTO> assembler;

  public EnderecoServiceImpl( EnderecoRepository repository,
                              EnderecoUtilitiesService enderecoUtilitiesService,
                              PessoaUtilitiesService pessoaUtilitiesService,
                              PagedResourcesAssembler<EnderecoResponseDTO> assembler) {

    this.repository = repository;
    this.enderecoUtilitiesService = enderecoUtilitiesService;
    this.pessoaUtilitiesService = pessoaUtilitiesService;
    this.assembler = assembler;
  }

  @Transactional
  public ResponseEntity<EnderecoResponseDTO> save( EnderecoRequestDTO enderecoRequest ) {
    validarLogradouro(enderecoRequest.getLogradouro());
    List<Pessoa> pessoas = pessoaUtilitiesService.criarListaPessoasPorId(enderecoRequest.getPessoasId());
    Endereco endereco = criarEnderecoESalvar(enderecoRequest, pessoas);
    EnderecoResponseDTO enderecoResponseDTO = criarEnderecoResponse(endereco);
    criarLinksHateoasDeEnderecoResponseDTO(enderecoResponseDTO, pessoas);
    pessoaUtilitiesService.atualizarEnderecoDasPessoas(pessoas, endereco);

    return new ResponseEntity<>(enderecoResponseDTO, HttpStatus.CREATED);
  }

  private void validarLogradouro(String logradouro) {
    if (repository.findByLogradouro(logradouro) != null)
      throw new EnderecoConflictException("Logradouro já cadastrado!");
  }

  private Endereco criarEnderecoESalvar(EnderecoRequestDTO enderecoRequestDTO, List<Pessoa> pessoas) {
    Endereco endereco = DozerMapper.parseObject(enderecoRequestDTO, Endereco.class);
    endereco.setPessoas(pessoas);
    return repository.save(endereco);
  }

  private EnderecoResponseDTO criarEnderecoResponse(Endereco endereco) {
    return DozerMapper.parseObject(endereco, EnderecoResponseDTO.class);
  }

  private void criarLinksHateoasDeEnderecoResponseDTO(EnderecoResponseDTO enderecoResponseDTO, List<Pessoa> pessoas) {
    criarLinkHateoasMoradores(enderecoResponseDTO, pessoas);
    criarLinkHateoasSelfRel(enderecoResponseDTO);
  }

  private void criarLinkHateoasMoradores(EnderecoResponseDTO enderecoResponseDTO, List<Pessoa> pessoas) {
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

  public ResponseEntity<EnderecoResponseDTO> findById( Long id ) {
    Endereco endereco = enderecoUtilitiesService.buscarEndereco(id);
    EnderecoResponseDTO enderecoResponseDTO = criarEnderecoResponse(endereco);
    criarLinksHateoasDeEnderecoResponseDTO(enderecoResponseDTO, endereco.getPessoas());

    return new ResponseEntity<>(enderecoResponseDTO, HttpStatus.OK);
  }

  public ResponseEntity<PagedModel<EntityModel<EnderecoResponseDTO>>> findAll( Pageable pageable ) {
    Page<Endereco> enderecoPage = criarPageEndereco(pageable);
    Page<EnderecoResponseDTO> enderecoResponseDTOS = converterPageEnderecoEmPageEnderecoResponseDTO(enderecoPage);
    criarLinksHateoasPageEnderecoResponseDTO(enderecoResponseDTOS, enderecoPage);

    Link link = new HateoasLinkBuilder().gerarLink(EnderecoRestController.class, "findAll");

    return new ResponseEntity<>(assembler.toModel(enderecoResponseDTOS, link), HttpStatus.OK);
  }

  private Page<Endereco> criarPageEndereco(Pageable pageable) {
    Page<Endereco> enderecoPage = repository.findAll(pageable);
    if (enderecoPage.isEmpty())
      throw new EmptyListException("Não existem endereços cadastrados!");

    return enderecoPage;
  }

  private Page<EnderecoResponseDTO> converterPageEnderecoEmPageEnderecoResponseDTO(Page<Endereco> enderecoPage) {
    return enderecoPage.map(
            endereco -> DozerMapper.parseObject(endereco, EnderecoResponseDTO.class)
    );
  }

  private void criarLinksHateoasPageEnderecoResponseDTO(Page<EnderecoResponseDTO> enderecoResponseDTOS,
                                                        Page<Endereco> enderecoPage) {

    enderecoResponseDTOS.forEach(enderecoResponse -> enderecoPage.forEach(endereco -> {
      if (enderecoResponse.getId().equals(endereco.getId())) {
        criarLinksHateoasDeEnderecoResponseDTO(enderecoResponse, endereco.getPessoas());
      }
    }));
  }

  @Transactional
  public ResponseEntity<EnderecoResponseDTO> update( EnderecoRequestDTO enderecoRequest, Long id ) {
    validarLogradouro(enderecoRequest.getLogradouro());
    Endereco endereco = enderecoUtilitiesService.buscarEndereco(id);
    endereco = atualizarEnderecoESalvar(enderecoRequest, endereco.getPessoas(), id);
    EnderecoResponseDTO enderecoResponse = criarEnderecoResponse(endereco);
    criarLinksHateoasDeEnderecoResponseDTO(enderecoResponse, endereco.getPessoas());

    return new ResponseEntity<>(enderecoResponse, HttpStatus.OK);
  }

  private Endereco atualizarEnderecoESalvar(EnderecoRequestDTO enderecoRequestDTO, List<Pessoa> pessoas , Long id) {
    Endereco endereco = DozerMapper.parseObject(enderecoRequestDTO, Endereco.class);
    endereco.setId(id);
    if (enderecoRequestDTO.getPessoasId() != null) {
      List<Pessoa> novasPessoas = pessoaUtilitiesService.criarListaPessoasPorId(
              enderecoRequestDTO.getPessoasId());
      pessoaUtilitiesService.atualizarEnderecoDasPessoas(novasPessoas, endereco);
      pessoas.addAll(novasPessoas);
    }
    endereco.setPessoas(pessoas);
    return repository.save(endereco);
  }

  public ResponseEntity<PagedModel<EntityModel<EnderecoResponseDTO>>> findByLogradouro(String logradouro,
                                                                                       Pageable pageable) {

    Page<Endereco> enderecoPage = criarPageEnderecoPorLogradouro(logradouro, pageable);
    Page<EnderecoResponseDTO> enderecoResponseDTOS = converterPageEnderecoEmPageEnderecoResponseDTO(enderecoPage);
    criarLinksHateoasPageEnderecoResponseDTO(enderecoResponseDTOS, enderecoPage);

    Link link = new HateoasLinkBuilder().gerarLinkFiltrando(
            EnderecoRestController.class, "findByLogradouro", logradouro);

    return new ResponseEntity<>(assembler.toModel(enderecoResponseDTOS, link), HttpStatus.OK);
  }

  private Page<Endereco> criarPageEnderecoPorLogradouro(String logradouro, Pageable pageable) {
    logradouro = "%" + logradouro + "%";
    Page<Endereco> enderecoPage = repository.findByLogradouro(logradouro, pageable);

    if (enderecoPage.isEmpty()) {
      throw new EmptyListException("Não existem endereços cadastrados com este logradouro!");
    }

    return enderecoPage;
  }

}
