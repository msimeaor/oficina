package io.github.msimeaor.aplicacao.model.service.impl;

import io.github.msimeaor.aplicacao.controller.PessoaRestController;
import io.github.msimeaor.aplicacao.exceptions.endereco.EnderecoConflictException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class EnderecoServiceImpl implements EnderecoService {

  private EnderecoRepository repository;
  private PessoaRepository pessoaRepository;

  public EnderecoServiceImpl( EnderecoRepository repository, PessoaRepository pessoaRepository ) {
    this.repository = repository;
    this.pessoaRepository = pessoaRepository;
  }

  @Transactional
  public ResponseEntity<EnderecoResponseDTO> save( EnderecoRequestDTO enderecoRequest ) {
    if (logradouroExists(enderecoRequest.getLogradouro())) {
      throw new EnderecoConflictException("Logradouro já cadastrado!");
    }

    Endereco endereco = DozerMapper.parseObject(enderecoRequest, Endereco.class);
    endereco = repository.save(endereco);

    List<Pessoa> pessoas = new ArrayList<>();
    if (enderecoRequest.getPessoas() != null) {
      pessoas = enderecoRequest.getPessoas().stream()
              .map(id -> pessoaRepository.findById(id)
                      .orElseThrow(() -> new PessoaNotFoundException("Cliente não encontrado! ID: " + id)))
              .collect(Collectors.toList());
    }

    EnderecoResponseDTO enderecoResponse = DozerMapper.parseObject(endereco, EnderecoResponseDTO.class);

    if (!pessoas.isEmpty()) {
      for (Pessoa pessoa : pessoas) {
        pessoa.setEndereco(endereco);
        pessoaRepository.save(pessoa);

        // TODO adicionar link HATEOAS para findById de EnderecoRestController
        enderecoResponse.add(linkTo(methodOn(PessoaRestController.class)
                .findById(pessoa.getId())).withRel("Morador(es)"));
      }
    }

    return new ResponseEntity<>(enderecoResponse, HttpStatus.CREATED);
  }

  private boolean logradouroExists( String logradouro ) {
    return repository.findByLogradouro(logradouro) != null;
  }

  public ResponseEntity<EnderecoResponseDTO> findById( Long id ) {
    Endereco endereco = repository.findById(id)
            .orElseThrow(() -> new EnderecoNotFoundException("Endereço não encontrado! ID: " + id));

    EnderecoResponseDTO enderecoResponse = DozerMapper.parseObject(endereco, EnderecoResponseDTO.class);

    if (endereco.getPessoas() != null) {
      for (Pessoa pessoa : endereco.getPessoas()) {
        enderecoResponse.add(linkTo(methodOn(PessoaRestController.class)
                .findById(pessoa.getId())).withRel("Morador(es)"));
      }
    } else {
      System.out.println("Null");
    }

    return new ResponseEntity<>(enderecoResponse, HttpStatus.OK);
  }

}
