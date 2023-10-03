package io.github.msimeaor.aplicacao.model.service.impl;

import io.github.msimeaor.aplicacao.controller.EnderecoRestController;
import io.github.msimeaor.aplicacao.controller.PessoaRestController;
import io.github.msimeaor.aplicacao.exceptions.pessoa.PessoaNotFoundException;
import io.github.msimeaor.aplicacao.mapper.DozerMapper;
import io.github.msimeaor.aplicacao.model.dto.request.EnderecoRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.EnderecoResponseDTO;
import io.github.msimeaor.aplicacao.model.entity.Endereco;
import io.github.msimeaor.aplicacao.model.entity.Pessoa;
import io.github.msimeaor.aplicacao.model.repository.EnderecoRepository;
import io.github.msimeaor.aplicacao.model.repository.PessoaRepository;
import io.github.msimeaor.aplicacao.model.service.EnderecoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Service
public class EnderecoServiceImpl implements EnderecoService {

  private EnderecoRepository repository;
  private PessoaRepository pessoaRepository;

  public EnderecoServiceImpl( EnderecoRepository repository, PessoaRepository pessoaRepository ) {
    this.repository = repository;
    this.pessoaRepository = pessoaRepository;
  }

  public ResponseEntity<EnderecoResponseDTO> save( EnderecoRequestDTO enderecoRequest ) {
    if (logradouroExists(enderecoRequest.getLogradouro())) {
      throw new EnderecoConflictException("Logradouro já cadastrado!");
    }

    Endereco endereco = DozerMapper.parseObject(enderecoRequest, Endereco.class);

    List<Pessoa> pessoas = new ArrayList<>();
    if (!enderecoRequest.getPessoas().isEmpty()) {
      for (Long id : enderecoRequest.getPessoas()) {
        Pessoa pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new PessoaNotFoundException("Cliente não encontrado! ID: " + id));

        pessoas.add(pessoa);
      }

      endereco.setPessoas(pessoas);
    }

    endereco = repository.save(endereco);

    EnderecoResponseDTO enderecoResponse = DozerMapper.parseObject(endereco, EnderecoResponseDTO.class);

    if (!pessoas.isEmpty()) {
      for (Pessoa p : pessoas) {
        enderecoResponse.add(linkTo(methodOn(PessoaRestController.class)
                .findById(p.getId())).withRel("Morador"));
      }
    }

    return new ResponseEntity<>(enderecoResponse, HttpStatus.CREATED);
  }

  private boolean logradouroExists( String logradouro ) {
    return repository.findByLogradouro(logradouro) != null;
  }

}
