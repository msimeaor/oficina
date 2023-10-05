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
import org.springframework.beans.BeanUtils;
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

    Endereco endereco = criarEndereco(pessoaRequest.getEnderecoId());

    Pessoa pessoa = DozerMapper.parseObject(pessoaRequest, Pessoa.class);
    pessoa.setEndereco(endereco);
    pessoa = repository.save(pessoa);

    var pessoaResponseDTO = converterPessoaEmPessoaResponseDTO(pessoa);

    pessoaResponseDTO.add(linkTo(methodOn(PessoaRestController.class)
            .findById(pessoaResponseDTO.getId())).withSelfRel());

    return new ResponseEntity<>(pessoaResponseDTO, HttpStatus.CREATED);
  }

  private void validarCadastroExistente(String nome, String placa) {
    if (repository.findByNome(nome).isPresent() && veiculoRepository.findByPlaca(placa).isPresent())
      throw new PessoaConflictException("Cliente já cadastrado!");
  }

  private Endereco criarEndereco(Long enderecoId) {
    if (enderecoId == null)
      return null;

    return enderecoRepository.findById(enderecoId)
            .orElseThrow(() -> new EnderecoNotFoundException("Endereço não encontrado! ID: " + enderecoId));
  }

  private PessoaResponseDTO converterPessoaEmPessoaResponseDTO(Pessoa pessoa) {
    List<TelefoneResponseDTO> telefoneResponse = converterListaTelefoneEmListaTelefoneResponse(pessoa.getTelefones());
    EnderecoResponseDTO enderecoResponse = converterEnderecoEmEnderecoResponseDTO(pessoa.getEndereco());

    PessoaResponseDTO pessoaResponse = DozerMapper.parseObject(pessoa, PessoaResponseDTO.class);
    pessoaResponse.setTelefonesResponse(telefoneResponse);
    pessoaResponse.setEnderecoResponse(enderecoResponse);

    return pessoaResponse;
  }

  private List<TelefoneResponseDTO> converterListaTelefoneEmListaTelefoneResponse(List<Telefone> telefones) {
    if (telefones == null)
      return null;

    return telefones.stream().map(telefone -> {
      return DozerMapper.parseObject(telefone, TelefoneResponseDTO.class);
    }).collect(Collectors.toList());
  }

  private EnderecoResponseDTO converterEnderecoEmEnderecoResponseDTO(Endereco endereco) {
    if (endereco == null)
      return null;

    return DozerMapper.parseObject(endereco, EnderecoResponseDTO.class);
  }

  public ResponseEntity<PessoaResponseDTO> findById( Long id ) {
    Pessoa pessoa = repository.findById(id)
            .orElseThrow(() -> new PessoaNotFoundException("Cliente não encontrado! ID: " + id));

    var pessoaResponse = converterPessoaEmPessoaResponseDTO(pessoa);

    pessoaResponse.add(linkTo(methodOn(PessoaRestController.class)
            .findById(id)).withSelfRel());

    return new ResponseEntity<>(pessoaResponse, HttpStatus.OK);
  }

  public ResponseEntity<PagedModel<EntityModel<PessoaResponseDTO>>> findAll( Pageable pageable ) {
    Page<Pessoa> pessoas = repository.findAll(pageable);
    if (pessoas.isEmpty()) {
      throw new EmptyListException("Não existem clientes cadastrados!");
    }

    Page<PessoaResponseDTO> pessoaResponseDTOS = pessoas.map(
            pessoa -> converterPessoaEmPessoaResponseDTO(pessoa)
    );

    pessoaResponseDTOS.map(
            pessoa -> pessoa.add(linkTo(methodOn(PessoaRestController.class)
                    .findById(pessoa.getId())).withSelfRel())
    );

    Link link = linkTo(methodOn(PessoaRestController.class)
            .findAll(pageable.getPageNumber(), pageable.getPageSize(), "ASC")).withSelfRel();

    return new ResponseEntity<>(assembler.toModel(pessoaResponseDTOS, link), HttpStatus.OK);
  }

  @Transactional
  public ResponseEntity<PessoaResponseDTO> update( PessoaRequestDTO pessoaRequest, Long id ) {
    Pessoa pessoa = repository.findById(id)
            .orElseThrow(() -> new PessoaNotFoundException("Cliente não encontrado! ID: " + id));

    Endereco endereco = criarEndereco(pessoaRequest.getEnderecoId());

    BeanUtils.copyProperties(pessoaRequest, pessoa);
    pessoa.setId(id);
    pessoa.setEndereco(endereco);
    pessoa = repository.save(pessoa);

    var pessoaResponse = converterPessoaEmPessoaResponseDTO(pessoa);

    pessoaResponse.add(linkTo(methodOn(PessoaRestController.class).findById(id)).withSelfRel());

    return new ResponseEntity<>(pessoaResponse, HttpStatus.OK);
  }

}
