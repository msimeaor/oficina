package io.github.msimeaor.aplicacao.model.service.utilities;

import io.github.msimeaor.aplicacao.exceptions.pessoa.PessoaNotFoundException;
import io.github.msimeaor.aplicacao.model.entity.Endereco;
import io.github.msimeaor.aplicacao.model.entity.Pessoa;
import io.github.msimeaor.aplicacao.model.entity.Telefone;
import io.github.msimeaor.aplicacao.model.repository.PessoaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PessoaUtilitiesService {

  private PessoaRepository repository;

  public PessoaUtilitiesService( PessoaRepository repository ) {
    this.repository = repository;
  }

  public Pessoa buscarPessoa(Long id) {
    return repository.findById(id)
            .orElseThrow(() -> new PessoaNotFoundException("Cliente não encontrado! ID: " + id));
  }

  public List<Pessoa> criarListaPessoasPorId(List<Long> pessoasIds) {
    if (pessoasIds == null)
      return null;

    return pessoasIds.stream().map(pessoaId -> repository.findById(pessoaId)
            .orElseThrow(() -> new PessoaNotFoundException("Cliente não encontrado! ID: " + pessoaId)))
            .collect(Collectors.toList());
  }

  public void atualizarEnderecoDasPessoas(List<Pessoa> pessoas, Endereco endereco) {
    if (pessoas != null)
      pessoas.forEach(pessoa -> pessoa.setEndereco(endereco));
  }

  public void AdicionarNovoTelefoneParaPessoa(Pessoa pessoa, Telefone telefone) {
    List<Telefone> telefones;

    if (pessoa.getTelefones() == null)
      telefones = new ArrayList<>();
    else
      telefones = pessoa.getTelefones();

    telefones.add(telefone);
    pessoa.setTelefones(telefones);
  }

}
