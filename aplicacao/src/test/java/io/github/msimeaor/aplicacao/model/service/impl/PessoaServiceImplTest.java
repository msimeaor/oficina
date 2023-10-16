package io.github.msimeaor.aplicacao.model.service.impl;

import io.github.msimeaor.aplicacao.enums.Fabricantes;
import io.github.msimeaor.aplicacao.enums.UFs;
import io.github.msimeaor.aplicacao.exceptions.endereco.EnderecoNotFoundException;
import io.github.msimeaor.aplicacao.exceptions.geral.EmptyListException;
import io.github.msimeaor.aplicacao.exceptions.pessoa.PessoaConflictException;
import io.github.msimeaor.aplicacao.exceptions.pessoa.PessoaNotFoundException;
import io.github.msimeaor.aplicacao.model.dto.request.PessoaRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.EnderecoResponseDTO;
import io.github.msimeaor.aplicacao.model.dto.response.PessoaResponseDTO;
import io.github.msimeaor.aplicacao.model.dto.response.TelefoneResponseDTO;
import io.github.msimeaor.aplicacao.model.entity.Endereco;
import io.github.msimeaor.aplicacao.model.entity.Pessoa;
import io.github.msimeaor.aplicacao.model.entity.Telefone;
import io.github.msimeaor.aplicacao.model.entity.Veiculo;
import io.github.msimeaor.aplicacao.model.repository.EnderecoRepository;
import io.github.msimeaor.aplicacao.model.repository.PessoaRepository;
import io.github.msimeaor.aplicacao.model.repository.VeiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class PessoaServiceImplTest {

  @InjectMocks
  private PessoaServiceImpl pessoaService;
  @Mock
  private PessoaRepository repository;
  @Mock
  private VeiculoRepository veiculoRepository;
  @Mock
  private EnderecoRepository enderecoRepository;
  @Mock
  private PagedResourcesAssembler<PessoaResponseDTO> assembler;

  private PessoaRequestDTO pessoaRequestDTO;
  private Pessoa pessoa;
  private Endereco endereco;
  private Veiculo veiculo;
  private Telefone telefone;
  private Page<Pessoa> pessoaPage;
  private Pageable pageable;

  private static final String NOME = "Nome Test";
  private static final String CPF = "000.000.000-00";
  private static final String EMAIL = "Email Test";
  private static final String SEXO = "MASCULINO";
  private static final LocalDate DATA_NASCIMENTO = LocalDate.of(2000, 01, 01);
  private static final Long ID = 1L;
  private static final String PLACA = "JJJ1111";
  private static final String LOGRADOURO = "Logradouro Test";
  private static final UFs UF = UFs.DF;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    startAttributtes();
  }

  @Test
  void whenSaveThenReturnSuccess() {
    when(repository.findByNome(anyString())).thenReturn(Optional.empty());
    when(enderecoRepository.findById(anyLong())).thenReturn(Optional.of(endereco));
    when(repository.save(any(Pessoa.class))).thenReturn(pessoa);

    var response = pessoaService.save(pessoaRequestDTO, PLACA);

    assertNotNull(response);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(response.getBody().getClass(), PessoaResponseDTO.class);

    assertEquals(ID ,response.getBody().getId());
    assertEquals(SEXO, response.getBody().getSexo());
    assertEquals(NOME, response.getBody().getNome());

    assertEquals("</api/pessoas/1>;rel=\"self\"", response.getBody().getLinks().toString());
  }

  @Test
  void whenValidarCadastroExistenteThenReturnPessoaConflictException() {
    when(repository.findByNome(anyString())).thenReturn(Optional.of(pessoa));
    when(veiculoRepository.findByPlaca(anyString())).thenReturn(Optional.of(veiculo));

    try {
      pessoaService.validarCadastroExistente(NOME, PLACA);

    } catch (Exception ex) {
      assertEquals(PessoaConflictException.class, ex.getClass());
      assertEquals("Cliente já cadastrado!", ex.getMessage());
    }
  }

  @Test
  void whenBuscarEnderecoThenReturnEnderecoNotFoundException() {
    when(enderecoRepository.findById(anyLong())).thenReturn(Optional.empty());

    try {
      var response = pessoaService.buscarEndereco(2L);

    } catch (Exception ex) {
      assertEquals(EnderecoNotFoundException.class, ex.getClass());
      assertEquals("Endereço não encontrado! ID: " + 2L, ex.getMessage());
    }
  }

  @Test
  void whenConverterListaTelefoneEmListaTelefoneResponseDTOThenReturnSuccess() {
    var response = pessoaService.converterListaTelefoneEmListaTelefoneResponseDTO(
            Collections.singletonList(telefone));

    assertNotNull(response);
    assertEquals(TelefoneResponseDTO.class, response.get(0).getClass());
    assertEquals(ID, response.get(0).getId());
  }

  @Test
  void whenConverterListaTelefoneEmListaTelefoneResponseDTOThenReturnNull() {
    var response = pessoaService.converterListaTelefoneEmListaTelefoneResponseDTO(null);

    assertEquals(null, response);
  }

  @Test
  void whenConverterEnderecoEmEnderecoResponseDTOThenReturnSuccess() {
    var response = pessoaService.converterEnderecoEmEnderecoResponseDTO(endereco);

    assertNotNull(response);
    assertEquals(EnderecoResponseDTO.class, response.getClass());
    assertEquals(ID, endereco.getId());
  }

  @Test
  void whenConverterEnderecoEmEnderecoResponseDTOThenReturnNull() {
    var response = pessoaService.converterEnderecoEmEnderecoResponseDTO(null);

    assertEquals(null, response);
  }

  @Test
  void whenFindByIdThenReturnSuccess() {
    when(repository.findById(anyLong())).thenReturn(Optional.of(pessoa));

    var response = pessoaService.findById(ID);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(response.getBody().getClass(), PessoaResponseDTO.class);

    assertEquals(ID ,response.getBody().getId());
    assertEquals("</api/pessoas/1>;rel=\"self\"", response.getBody().getLinks().toString());
  }

  @Test
  void whenBuscarPessoaTheReturnPessoaNotFoundException() {
    when(repository.findById(anyLong())).thenReturn(Optional.empty());

    try {
      var response = pessoaService.buscarPessoa(2L);

    } catch (Exception ex) {
      assertEquals(PessoaNotFoundException.class, ex.getClass());
      assertEquals("Cliente não encontrado! ID: " + 2L, ex.getMessage());
    }
  }

  // TODO completar depois
  @Test
  void whenFindAllThenReturnSuccess() {}

  @Test
  void whenCriarPagePessoaThenReturnSuccess() {
    when(repository.findAll(any(Pageable.class))).thenReturn(pessoaPage);

    var response = pessoaService.criarPagePessoa(pageable);

    assertNotNull(response);
    assertEquals(PageImpl.class, response.getClass());
    response.forEach(pessoaPage -> {
      assertNotNull(pessoaPage);
      assertEquals(Pessoa.class, pessoaPage.getClass());
      assertEquals(ID, pessoaPage.getId());
    });
  }

  @Test
  void whenCriarPagePessoaThenReturnEmptyListException() {
    when(repository.findAll(any(Pageable.class))).thenReturn(Page.empty());

    try {
      var response = pessoaService.criarPagePessoa(pageable);

    } catch (Exception ex) {
      assertEquals(EmptyListException.class, ex.getClass());
      assertEquals("Não existem clientes cadastrados!", ex.getMessage());
    }
  }

  @Test
  void whenUpdateThenReturnSuccess() {
    when(repository.findById(anyLong())).thenReturn(Optional.of(pessoa));
    when(enderecoRepository.findById(anyLong())).thenReturn(Optional.of(endereco));
    pessoa.setNome("Nome updated");
    when(repository.save(any(Pessoa.class))).thenReturn(pessoa);

    var response = pessoaService.update(pessoaRequestDTO, ID);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(ID, response.getBody().getId());
    assertEquals("Nome updated", response.getBody().getNome());
  }

  // TODO completar depois
  @Test
  void whenFindByNomeLikeThenReturnSuccess() {}

  public void startAttributtes() {
    pessoaRequestDTO = PessoaRequestDTO.builder()
            .nome(NOME)
            .sexo(SEXO)
            .enderecoId(ID)
            .build();

    pessoa = Pessoa.builder()
            .id(ID)
            .nome(NOME)
            .sexo(SEXO)
            .build();

    endereco = Endereco.builder()
            .id(ID)
            .logradouro(LOGRADOURO)
            .uf(UF)
            .build();

    veiculo = Veiculo.builder()
            .id(ID)
            .nome("Veiculo Test")
            .fabricante(Fabricantes.AUDI)
            .placa(PLACA)
            .kmAtual("10.000")
            .build();

    telefone = Telefone.builder()
            .id(ID)
            .numero("61991979110")
            .pessoa(pessoa)
            .build();

    pageable = PageRequest.of(0, 10);
    List<Pessoa> pessoas = Arrays.asList(pessoa);
    pessoaPage = new PageImpl<>(pessoas, pageable, pessoas.size());
  }

}