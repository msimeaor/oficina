package io.github.msimeaor.aplicacao.model.service.impl;

import io.github.msimeaor.aplicacao.exceptions.pessoa.PessoaConflictException;
import io.github.msimeaor.aplicacao.mapper.DozerMapper;
import io.github.msimeaor.aplicacao.model.dto.request.PessoaRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.PessoaResponseDTO;
import io.github.msimeaor.aplicacao.model.entity.Pessoa;
import io.github.msimeaor.aplicacao.model.repository.PessoaRepository;
import io.github.msimeaor.aplicacao.model.repository.VeiculoRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PessoaServiceImpl {

  private PessoaRepository repository;
  private VeiculoRepository veiculoRepository;

  public PessoaServiceImpl( PessoaRepository repository, VeiculoRepository veiculoRepository ) {
    this.repository = repository;
    this.veiculoRepository = veiculoRepository;
  }

  // TODO desenvolver regra de persistir telefone e endereço caso seja passado no pessoaRequest
  @Transactional
  public ResponseEntity<PessoaResponseDTO> save( PessoaRequestDTO pessoaRequest, String placa ) {
    if (nomePessoaExiste(pessoaRequest.getNome()) && placaCarroExiste(placa)) {
      throw new PessoaConflictException("Cliente já cadastrado!");
    }

    var pessoa = DozerMapper.parseObject(pessoaRequest, Pessoa.class);
    pessoa = repository.save(pessoa);
    var pessoaResponseDTO = DozerMapper.parseObject(pessoa, PessoaResponseDTO.class);

    /*TODO descomentar após criar o método findById()
    pessoaResponseDTO.add(linkTo(methodOn(PessoaRestController.class)
            .findById(pessoaResponseDTO.getId())).withSelfRel());
    */

    return new ResponseEntity<>(pessoaResponseDTO, HttpStatus.CREATED);
  }

  private boolean nomePessoaExiste(String nome) {
    return repository.findByNome(nome).isPresent();
  }

  private boolean placaCarroExiste(String placa) {
    return veiculoRepository.findByPlaca(placa).isPresent();
  }

}
