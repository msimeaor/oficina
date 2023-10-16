package io.github.msimeaor.aplicacao.model.service.impl;

import io.github.msimeaor.aplicacao.enums.UFs;
import io.github.msimeaor.aplicacao.exceptions.endereco.EnderecoConflictException;
import io.github.msimeaor.aplicacao.exceptions.pessoa.PessoaConflictException;
import io.github.msimeaor.aplicacao.exceptions.pessoa.PessoaNotFoundException;
import io.github.msimeaor.aplicacao.model.dto.request.EnderecoRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.EnderecoResponseDTO;
import io.github.msimeaor.aplicacao.model.entity.Endereco;
import io.github.msimeaor.aplicacao.model.entity.Pessoa;
import io.github.msimeaor.aplicacao.model.repository.EnderecoRepository;
import io.github.msimeaor.aplicacao.model.repository.PessoaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;

import java.net.http.WebSocketHandshakeException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class EnderecoServiceImplTest {

  @InjectMocks
  private EnderecoServiceImpl enderecoService;
  @Mock
  private EnderecoRepository repository;
  @Mock
  private PessoaRepository pessoaRepository;
  @Mock
  private PagedResourcesAssembler<EnderecoResponseDTO> assembler;

  private Endereco endereco;
  private EnderecoRequestDTO enderecoRequestDTO;
  private Pessoa pessoa;
  private EnderecoResponseDTO enderecoResponseDTO;

  private static final Long ID = 1L;
  private static final String LOGRADOURO = "Logradouro Test";
  private static final UFs UF = UFs.DF;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    startAttributes();
  }

  @Test
  void whenSaveThenReturnSuccess() {
    when(repository.findByLogradouro(anyString())).thenReturn(null);
    when(repository.save(any(Endereco.class))).thenReturn(endereco);

    var response = enderecoService.save(enderecoRequestDTO);

    assertNotNull(response);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(response.getBody().getClass(), EnderecoResponseDTO.class);

    assertEquals(ID ,response.getBody().getId());
    assertEquals(LOGRADOURO, response.getBody().getLogradouro());
    assertEquals(UF, response.getBody().getUf());

    assertEquals("</api/enderecos/1>;rel=\"self\"", response.getBody().getLinks().toString());
    // Não estou validando os links Hateoas de moradores que são gerados no EnderecoResponseDTO
  }

  @Test
  void whenValidarLogradouroThenReturnEnderecoConflictException() {
    when(repository.findByLogradouro(anyString())).thenReturn(endereco);

    try {
      enderecoService.validarLogradouro(LOGRADOURO);

    } catch (Exception ex) {
      assertNotNull(ex);
      assertEquals(EnderecoConflictException.class, ex.getClass());
      assertEquals("Logradouro já cadastrado!", ex.getMessage());
    }
  }

  @Test
  void whenCriarListaPessoaPorIdThenReturnSuccess() {
    when(pessoaRepository.findById(anyLong())).thenReturn(Optional.of(pessoa));

    var response = enderecoService.criarListaPessoaPorId(Collections.singletonList(ID));

    assertNotNull(response);
    assertEquals(ArrayList.class, response.getClass());
    assertEquals(ID, response.get(0).getId());
  }

  @Test
  void whenCriarListaPessoaPorIdThenReturnPessoaNotFoundException() {
    when(pessoaRepository.findById(anyLong())).thenReturn(Optional.empty());

    try {
      var response = enderecoService.criarListaPessoaPorId(Collections.singletonList(2L));

    } catch (Exception ex) {
      assertNotNull(ex);
      assertEquals(PessoaNotFoundException.class, ex.getClass());
      assertEquals("Cliente não encontrado! ID: " + 2L, ex.getMessage());
    }
  }

  @Test
  void whenCriarListaPessoaPorIdThenReturnNull() {
    var response = enderecoService.criarListaPessoaPorId(null);

    assertEquals(null, response);
  }

  @Test
  void whenCriarEnderecoESalvarThenReturnSuccess() {
    when(repository.save(any(Endereco.class))).thenReturn(endereco);

    var response = enderecoService.criarEnderecoESalvar(enderecoRequestDTO, Collections.singletonList(pessoa));
    // Simulando o endereco.setPessoas do método
    response.setPessoas(Collections.singletonList(pessoa));

    assertNotNull(response);
    assertEquals(Endereco.class, response.getClass());
    assertEquals(ID, response.getId());
    assertEquals(pessoa.getId(), response.getPessoas().get(0).getId());
  }

  @Test
  void whenCriarEnderecoResponseThenReturnSuccess() {
    var response = enderecoService.criarEnderecoResponse(endereco);

    assertNotNull(response);
    assertEquals(EnderecoResponseDTO.class, response.getClass());
    assertEquals(endereco.getId(), response.getId());
  }

  @Test
  void whenCriarLinkHateoasMoradoresThenReturnSuccess() {
    enderecoService.criarLinkHateoasMoradores(enderecoResponseDTO, Collections.singletonList(pessoa));

    assertEquals("</api/pessoas/1>;rel=\"Morador(es)\"", enderecoResponseDTO.getLinks().toString());
  }

  @Test
  void whenCriarLinkHateoasMoradoresThenFail() {
    enderecoService.criarLinkHateoasMoradores(enderecoResponseDTO, null);

    assertTrue(enderecoResponseDTO.getLinks().hasSize(0));
  }

  @Test
  void whenAtualizarPessoaRelacionandoEnderecoThenReturnSuccess() {
    enderecoService.atualizarPessoaRelacionandoEndereco(Collections.singletonList(pessoa), endereco);

    assertNotNull(pessoa.getEndereco());
    assertEquals(endereco.getId(), pessoa.getEndereco().getId());
  }

  @Test
  void whenAtualizarPessoaRelacionandoEnderecoThenFail() {
    enderecoService.atualizarPessoaRelacionandoEndereco(null, endereco);

    assertNull(pessoa.getEndereco());
  }

  @Test
  void findById() {
  }

  @Test
  void findAll() {
  }

  @Test
  void update() {
  }

  private void startAttributes() {
    pessoa = Pessoa.builder()
            .id(ID)
            .nome("Nome Test")
            .sexo("MASCULINO")
            .build();

    endereco = Endereco.builder()
            .id(ID)
            .logradouro(LOGRADOURO)
            .uf(UF)
            .build();

    enderecoRequestDTO = EnderecoRequestDTO.builder()
            .logradouro(LOGRADOURO)
            .uf(UF)
            .build();

    enderecoResponseDTO = EnderecoResponseDTO.builder()
            .id(ID)
            .logradouro(LOGRADOURO)
            .uf(UF)
            .build();
  }

}