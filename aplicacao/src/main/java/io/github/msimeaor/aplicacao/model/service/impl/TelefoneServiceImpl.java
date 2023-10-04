package io.github.msimeaor.aplicacao.model.service.impl;

import io.github.msimeaor.aplicacao.controller.PessoaRestController;
import io.github.msimeaor.aplicacao.controller.TelefoneRestController;
import io.github.msimeaor.aplicacao.exceptions.geral.EmptyListException;
import io.github.msimeaor.aplicacao.exceptions.pessoa.PessoaNotFoundException;
import io.github.msimeaor.aplicacao.exceptions.telefone.TelefoneConflictException;
import io.github.msimeaor.aplicacao.exceptions.telefone.TelefoneNotFoundException;
import io.github.msimeaor.aplicacao.mapper.DozerMapper;
import io.github.msimeaor.aplicacao.model.dto.request.TelefoneRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.TelefoneResponseDTO;
import io.github.msimeaor.aplicacao.model.entity.Pessoa;
import io.github.msimeaor.aplicacao.model.entity.Telefone;
import io.github.msimeaor.aplicacao.model.repository.PessoaRepository;
import io.github.msimeaor.aplicacao.model.repository.TelefoneRepository;
import io.github.msimeaor.aplicacao.model.service.TelefoneService;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class TelefoneServiceImpl implements TelefoneService {

  private TelefoneRepository repository;
  private PessoaRepository pessoaRepository;
  private PagedResourcesAssembler<TelefoneResponseDTO> assembler;

  public TelefoneServiceImpl(TelefoneRepository repository,
                             PessoaRepository pessoaRepository,
                             PagedResourcesAssembler<TelefoneResponseDTO> assembler) {
    this.repository = repository;
    this.pessoaRepository = pessoaRepository;
    this.assembler = assembler;
  }

  @Transactional
  public ResponseEntity<TelefoneResponseDTO> save( TelefoneRequestDTO telefoneRequest ) {
    if (numeroExists(telefoneRequest.getNumero())) {
      throw new TelefoneConflictException("Numero já cadastrado!");
    }

    Pessoa pessoa = pessoaRepository.findById(telefoneRequest.getPessoaId())
            .orElseThrow(() ->
                    new PessoaNotFoundException("Cliente não encontrado! ID: " + telefoneRequest.getPessoaId()));

    Telefone telefone = DozerMapper.parseObject(telefoneRequest, Telefone.class);
    telefone.setPessoa(pessoa);
    telefone = repository.save(telefone);

    List<Telefone> pessoaTelefones = pessoa.getTelefones();
    pessoaTelefones.add(telefone);
    pessoa.setTelefones(pessoaTelefones);

    TelefoneResponseDTO telefoneResponse = DozerMapper.parseObject(telefone, TelefoneResponseDTO.class);

    telefoneResponse.add(linkTo(methodOn(TelefoneRestController.class)
            .findById(telefoneResponse.getId())).withSelfRel());
    telefoneResponse.add(linkTo(methodOn(PessoaRestController.class)
            .findById(telefone.getPessoa().getId())).withRel("Proprietário(a)"));

    return new ResponseEntity<>(telefoneResponse, HttpStatus.CREATED);
  }

  private boolean numeroExists(String numero) {
    return repository.findByNumero(numero) != null;
  }

  public ResponseEntity<TelefoneResponseDTO> findById( Long id ) {
    Telefone telefone = repository.findById(id)
            .orElseThrow(() -> new TelefoneNotFoundException("Telefone não encontrado! ID: " + id));

    TelefoneResponseDTO telefoneResponse = DozerMapper.parseObject(telefone, TelefoneResponseDTO.class);

    telefoneResponse.add(linkTo(methodOn(TelefoneRestController.class)
            .findById(telefoneResponse.getId())).withSelfRel());
    telefoneResponse.add(linkTo(methodOn(PessoaRestController.class)
            .findById(telefone.getPessoa().getId())).withRel("Proprietário(a)"));

    return new ResponseEntity<>(telefoneResponse, HttpStatus.OK);
  }

  public ResponseEntity<PagedModel<EntityModel<TelefoneResponseDTO>>> findAll( Pageable pageable ) {
    Page<Telefone> telefones = repository.findAll(pageable);
    if (telefones.isEmpty()) {
      throw new EmptyListException("Não existem telefones cadastrados!");
    }

    Page<TelefoneResponseDTO> telefoneResponseDTOS = telefones.map(
            telefone -> DozerMapper.parseObject(telefone, TelefoneResponseDTO.class)
    );

    telefoneResponseDTOS.forEach(telefone -> {
      telefone.add(linkTo(methodOn(TelefoneRestController.class)
              .findById(telefone.getId())).withSelfRel());

      for (Telefone t : telefones) {
        if (t.getId() == telefone.getId()) {
          telefone.add(linkTo(methodOn(PessoaRestController.class)
                  .findById(t.getPessoa().getId())).withRel("Propietário(a)"));
        }
      }
    });

    Link link = linkTo(methodOn(TelefoneRestController.class).findAll(
            pageable.getPageNumber(), pageable.getPageSize(), "ASC"
    )).withSelfRel();

    return new ResponseEntity<>(assembler.toModel(telefoneResponseDTOS, link), HttpStatus.OK);
  }

  @Transactional
  public ResponseEntity<TelefoneResponseDTO> update( TelefoneRequestDTO telefoneRequest, Long id ) {
    if (numeroExists(telefoneRequest.getNumero())) {
      throw new TelefoneConflictException("Numero já cadastrado!");
    }

    Pessoa pessoa = pessoaRepository.findById(telefoneRequest.getPessoaId())
            .orElseThrow(() ->
                    new PessoaNotFoundException("Cliente não encontrado! ID: " + telefoneRequest.getPessoaId()));

    Telefone telefone = repository.findById(id)
            .orElseThrow(() -> new TelefoneNotFoundException("Telefone não encontrado! ID: " + id));

    BeanUtils.copyProperties(telefoneRequest, telefone);
    telefone.setPessoa(pessoa);
    telefone.setId(id);
    telefone = repository.save(telefone);

    TelefoneResponseDTO telefoneResponse = DozerMapper.parseObject(telefone, TelefoneResponseDTO.class);

    telefoneResponse.add(linkTo(methodOn(TelefoneRestController.class)
            .findById(telefoneResponse.getId())).withSelfRel());
    telefoneResponse.add(linkTo(methodOn(PessoaRestController.class)
            .findById(pessoa.getId())).withRel("Propietário(a)"));

    return new ResponseEntity<>(telefoneResponse, HttpStatus.OK);
  }

}
