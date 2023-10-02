package io.github.msimeaor.aplicacao.model.service.impl;

import io.github.msimeaor.aplicacao.controller.PessoaRestController;
import io.github.msimeaor.aplicacao.exceptions.geral.EmptyListException;
import io.github.msimeaor.aplicacao.exceptions.pessoa.PessoaConflictException;
import io.github.msimeaor.aplicacao.exceptions.pessoa.PessoaNotFoundException;
import io.github.msimeaor.aplicacao.mapper.DozerMapper;
import io.github.msimeaor.aplicacao.model.dto.request.PessoaRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.PessoaResponseDTO;
import io.github.msimeaor.aplicacao.model.entity.Pessoa;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Service
public class PessoaServiceImpl {

  private PessoaRepository repository;
  private VeiculoRepository veiculoRepository;
  private PagedResourcesAssembler<PessoaResponseDTO> assembler;

  public PessoaServiceImpl( PessoaRepository repository,
                            VeiculoRepository veiculoRepository,
                            PagedResourcesAssembler<PessoaResponseDTO> assembler ) {

    this.repository = repository;
    this.veiculoRepository = veiculoRepository;
    this.assembler = assembler;
  }

  /*
    TODO desenvolver regra de persistir telefone e endereço caso seja passado no pessoaRequest
    TODO descomentar após criar o método findById()
  */
  @Transactional
  public ResponseEntity<PessoaResponseDTO> save( PessoaRequestDTO pessoaRequest, String placa ) {
    if (nomePessoaExiste(pessoaRequest.getNome()) && placaCarroExiste(placa)) {
      throw new PessoaConflictException("Cliente já cadastrado!");
    }

    var pessoa = DozerMapper.parseObject(pessoaRequest, Pessoa.class);
    pessoa = repository.save(pessoa);
    var pessoaResponseDTO = DozerMapper.parseObject(pessoa, PessoaResponseDTO.class);


    pessoaResponseDTO.add(linkTo(methodOn(PessoaRestController.class)
            .findById(pessoaResponseDTO.getId())).withSelfRel());

    return new ResponseEntity<>(pessoaResponseDTO, HttpStatus.CREATED);
  }

  private boolean nomePessoaExiste(String nome) {
    return repository.findByNome(nome).isPresent();
  }

  private boolean placaCarroExiste(String placa) {
    return veiculoRepository.findByPlaca(placa).isPresent();
  }

  public ResponseEntity<PessoaResponseDTO> findById( Long id ) {
    Pessoa pessoa = repository.findById(id)
            .orElseThrow(() -> new PessoaNotFoundException("Não existem clientes para o id " + id));

    PessoaResponseDTO pessoaResponse = DozerMapper.parseObject(pessoa, PessoaResponseDTO.class);

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
            pessoa -> DozerMapper.parseObject(pessoa, PessoaResponseDTO.class)
    );

    pessoaResponseDTOS.map(
            pessoa -> pessoa.add(linkTo(methodOn(PessoaRestController.class)
                    .findById(pessoa.getId())).withSelfRel())
    );

    Link link = linkTo(methodOn(PessoaRestController.class)
            .findAll(pageable.getPageNumber(), pageable.getPageSize(), "ASC")).withSelfRel();

    return new ResponseEntity<>(assembler.toModel(pessoaResponseDTOS, link), HttpStatus.OK);
  }

}
