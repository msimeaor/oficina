package io.github.msimeaor.aplicacao.model.service.impl;

import io.github.msimeaor.aplicacao.controller.EnderecoRestController;
import io.github.msimeaor.aplicacao.controller.PessoaRestController;
import io.github.msimeaor.aplicacao.exceptions.endereco.EnderecoConflictException;
import io.github.msimeaor.aplicacao.exceptions.endereco.EnderecoNotFoundException;
import io.github.msimeaor.aplicacao.exceptions.geral.EmptyListException;
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
public class EnderecoServiceImpl implements EnderecoService {

  private EnderecoRepository repository;
  private PessoaRepository pessoaRepository;
  private PagedResourcesAssembler<EnderecoResponseDTO> assembler;

  public EnderecoServiceImpl( EnderecoRepository repository,
                              PessoaRepository pessoaRepository,
                              PagedResourcesAssembler<EnderecoResponseDTO> assembler) {
    this.repository = repository;
    this.pessoaRepository = pessoaRepository;
    this.assembler = assembler;
  }

  @Transactional
  public ResponseEntity<EnderecoResponseDTO> save( EnderecoRequestDTO enderecoRequest ) {
    validarLogradouro(enderecoRequest.getLogradouro());
    List<Pessoa> pessoas = criarListaPessoaPorId(enderecoRequest.getPessoasId());
    Endereco endereco = criarEnderecoESalvar(enderecoRequest, pessoas);
    EnderecoResponseDTO enderecoResponseDTO = criarEnderecoResponse(endereco);
    criarLinksHateoasDeEnderecoResponseDTO(enderecoResponseDTO, pessoas);
    atualizarPessoaRelacionandoEndereco(pessoas, endereco);

    return new ResponseEntity<>(enderecoResponseDTO, HttpStatus.CREATED);
  }

  private void validarLogradouro(String logradouro) {
    if (repository.findByLogradouro(logradouro) != null)
      throw new EnderecoConflictException("Logradouro já cadastrado!");
  }

  private List<Pessoa> criarListaPessoaPorId(List<Long> pessoasId) {
    if (pessoasId == null)
      return null;

    return pessoasId.stream().map((pessoaId) -> {
      return pessoaRepository.findById(pessoaId)
              .orElseThrow(() -> new PessoaNotFoundException("Cliente não encontrado! ID: " + pessoaId));
    }).collect(Collectors.toList());
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

  private void atualizarPessoaRelacionandoEndereco(List<Pessoa> pessoas, Endereco endereco) {
    pessoas.forEach(
            pessoa -> pessoa.setEndereco(endereco)
    );
  }

  public ResponseEntity<EnderecoResponseDTO> findById( Long id ) {
    Endereco endereco = repository.findById(id)
            .orElseThrow(() -> new EnderecoNotFoundException("Endereço não encontrado! ID: " + id));

    EnderecoResponseDTO enderecoResponse = DozerMapper.parseObject(endereco, EnderecoResponseDTO.class);

    enderecoResponse.add(linkTo(methodOn(EnderecoRestController.class).findById(id)).withSelfRel());
    endereco.getPessoas().forEach(pessoa -> {
      enderecoResponse.add(linkTo(methodOn(PessoaRestController.class)
              .findById(pessoa.getId())).withRel("Morador(es)"));
    });

    return new ResponseEntity<>(enderecoResponse, HttpStatus.OK);
  }

  public ResponseEntity<PagedModel<EntityModel<EnderecoResponseDTO>>> findAll( Pageable pageable ) {
    Page<Endereco> enderecos = repository.findAll(pageable);
    if (enderecos.isEmpty()) {
      throw new EmptyListException("Não existem endereços cadastrados!");
    }

    Page<EnderecoResponseDTO> enderecoResponseDTOS = enderecos.map(
            endereco -> DozerMapper.parseObject(endereco, EnderecoResponseDTO.class)
    );

    enderecoResponseDTOS.forEach(enderecoResponse -> {
      enderecoResponse.add(linkTo(methodOn(EnderecoRestController.class)
              .findById(enderecoResponse.getId())).withSelfRel());

      for (Endereco endereco : enderecos) {
        endereco.getPessoas().forEach(pessoa -> {
          if (pessoa.getEndereco().getId() == enderecoResponse.getId()) {
            enderecoResponse.add(linkTo(methodOn(PessoaRestController.class)
                    .findById(pessoa.getId())).withRel("Morador(es)"));
          }
        });
      }
    });


    Link link = linkTo(methodOn(EnderecoRestController.class).findAll(
            pageable.getPageNumber(), pageable.getPageSize(), "ASC")).withSelfRel();

    return new ResponseEntity<>(assembler.toModel(enderecoResponseDTOS, link), HttpStatus.OK);
  }

  @Transactional
  public ResponseEntity<EnderecoResponseDTO> update( EnderecoRequestDTO enderecoRequest, Long id ) {
    validarLogradouro(enderecoRequest.getLogradouro());

    Endereco endereco = repository.findById(id)
            .orElseThrow(() -> new EnderecoNotFoundException("Endereço não encontrado! ID: " + id));

    BeanUtils.copyProperties(enderecoRequest, endereco);
    endereco.setId(id);
    endereco = repository.save(endereco);

    EnderecoResponseDTO enderecoResponse = DozerMapper.parseObject(endereco, EnderecoResponseDTO.class);

    enderecoResponse.add(linkTo(methodOn(EnderecoRestController.class)
            .findById(id)).withSelfRel());

    if (endereco.getPessoas() != null) {
      for (Pessoa pessoa : endereco.getPessoas()) {
        enderecoResponse.add(linkTo(methodOn(PessoaRestController.class)
                .findById(pessoa.getId())).withRel("Morador(es)"));
      }
    }

    return new ResponseEntity<>(enderecoResponse, HttpStatus.OK);
  }

}
