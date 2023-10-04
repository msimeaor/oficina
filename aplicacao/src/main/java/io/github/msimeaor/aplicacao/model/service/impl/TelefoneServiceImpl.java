package io.github.msimeaor.aplicacao.model.service.impl;

import io.github.msimeaor.aplicacao.controller.PessoaRestController;
import io.github.msimeaor.aplicacao.controller.TelefoneRestController;
import io.github.msimeaor.aplicacao.exceptions.pessoa.PessoaNotFoundException;
import io.github.msimeaor.aplicacao.exceptions.telefone.TelefoneConflictException;
import io.github.msimeaor.aplicacao.mapper.DozerMapper;
import io.github.msimeaor.aplicacao.model.dto.request.TelefoneRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.TelefoneResponseDTO;
import io.github.msimeaor.aplicacao.model.entity.Pessoa;
import io.github.msimeaor.aplicacao.model.entity.Telefone;
import io.github.msimeaor.aplicacao.model.repository.PessoaRepository;
import io.github.msimeaor.aplicacao.model.repository.TelefoneRepository;
import io.github.msimeaor.aplicacao.model.service.TelefoneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Service
public class TelefoneServiceImpl implements TelefoneService {

  private TelefoneRepository repository;
  private PessoaRepository pessoaRepository;

  public TelefoneServiceImpl(TelefoneRepository repository,
                             PessoaRepository pessoaRepository) {
    this.repository = repository;
    this.pessoaRepository = pessoaRepository;
  }

  public ResponseEntity<TelefoneResponseDTO> save( TelefoneRequestDTO telefoneRequest ) {
    if (numeroExists(telefoneRequest.getNumero())) {
      throw new TelefoneConflictException("Numero já cadastrado!");
    }

    Pessoa pessoa = pessoaRepository.findById(telefoneRequest.getPessoa())
            .orElseThrow(() ->
                    new PessoaNotFoundException("Cliente não encontrado! ID: " + telefoneRequest.getPessoa()));

    Telefone telefone = DozerMapper.parseObject(telefoneRequest, Telefone.class);
    telefone.setPessoa(pessoa);
    telefone = repository.save(telefone);

    List<Telefone> pessoaTelefones;

    if (pessoa.getTelefones() == null) {
      pessoaTelefones = new ArrayList<>();
    } else {
      pessoaTelefones = pessoa.getTelefones();
    }

    pessoaTelefones.add(telefone);
    pessoa.setTelefones(pessoaTelefones);

    TelefoneResponseDTO telefoneResponse = DozerMapper.parseObject(telefone, TelefoneResponseDTO.class);

    /* TODO descomentar quando criar o endpoint findById
    telefoneResponse.add(linkTo(methodOn(TelefoneRestController.class)
            .findById(telefoneResponse.getId())).withSelfRel());
    */
    telefoneResponse.add(linkTo(methodOn(PessoaRestController.class)
            .findById(telefone.getPessoa().getId())).withRel("Proprietário(a)"));

    return new ResponseEntity<>(telefoneResponse, HttpStatus.CREATED);
  }

  private boolean numeroExists(String numero) {
    return repository.findByNumero(numero) != null;
  }

}
