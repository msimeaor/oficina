package io.github.msimeaor.aplicacao.model.service.impl;

import io.github.msimeaor.aplicacao.exceptions.pessoa.PessoaNotFoundException;
import io.github.msimeaor.aplicacao.exceptions.telefone.TelefoneConflictException;
import io.github.msimeaor.aplicacao.model.dto.request.TelefoneRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.TelefoneResponseDTO;
import io.github.msimeaor.aplicacao.model.entity.Pessoa;
import io.github.msimeaor.aplicacao.model.entity.Telefone;
import io.github.msimeaor.aplicacao.model.repository.PessoaRepository;
import io.github.msimeaor.aplicacao.model.repository.TelefoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class TelefoneServiceImplTest {

  @InjectMocks
  private TelefoneServiceImpl telefoneService;
  @Mock
  private TelefoneRepository repository;
  @Mock
  private PessoaRepository pessoaRepository;

  private Pessoa pessoa;
  private Telefone telefone;
  private TelefoneRequestDTO telefoneRequestDTO;
  private TelefoneResponseDTO telefoneResponseDTO;

  private static final Long ID = 1L;
  private static final String NUMERO = "00000000000";


  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    startAttributes();
  }

  @Test
  void whenSaveThenReturnSuccess() {
    when(repository.findByNumero(anyString())).thenReturn(null);
    when(pessoaRepository.findById(anyLong())).thenReturn(Optional.of(pessoa));
    when(repository.save(any(Telefone.class))).thenReturn(telefone);

    var response = telefoneService.save(telefoneRequestDTO);

    assertNotNull(response);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(TelefoneResponseDTO.class, response.getBody().getClass());

    assertEquals(ID ,response.getBody().getId());
    assertEquals(NUMERO, response.getBody().getNumero());
    assertEquals("</api/telefones/1>;rel=\"self\",</api/pessoas/1>;rel=\"Proprietário\"",
            response.getBody().getLinks().toString());
  }

  @Test
  void whenValidarNumeroThenReturnTelefoneConflictException() {
    when(repository.findByNumero(anyString())).thenReturn(telefone);

    try {
      telefoneService.validarNumero(NUMERO);

    } catch (Exception ex) {
      assertNotNull(ex);
      assertEquals(TelefoneConflictException.class, ex.getClass());
      assertEquals("Numero já cadastrado!", ex.getMessage());
    }
  }

  @Test
  void whenBuscarPessoaThenReturnSuccess() {
    when(pessoaRepository.findById(anyLong())).thenReturn(Optional.of(pessoa));

    var response = telefoneService.buscarPessoa(ID);

    assertNotNull(response);
    assertEquals(Pessoa.class, response.getClass());
    assertEquals(ID, response.getId());
  }

  @Test
  void whenBuscarPessoaThenReturnPessoaNotFoundException() {
    when(pessoaRepository.findById(anyLong())).thenReturn(Optional.empty());

    try {
      var response = telefoneService.buscarPessoa(2L);

    } catch (Exception ex) {
      assertNotNull(ex);
      assertEquals(PessoaNotFoundException.class, ex.getClass());
      assertEquals("Cliente não encontrado! ID: " + 2L, ex.getMessage());
    }
  }

  @Test
  void whenCriarTelefoneESalvarThenReturnSuccess() {
    when(repository.save(any(Telefone.class))).thenReturn(telefone);

    var response = telefoneService.criarTelefoneESalvar(telefoneRequestDTO, pessoa);

    assertNotNull(response);
    assertEquals(Telefone.class, response.getClass());
    assertEquals(ID, response.getId());
    assertNotNull(response.getPessoa());
    assertEquals(pessoa.getId(), response.getPessoa().getId());
  }

  @Test
  void whenAtualizarListaDeTelefonesDaPessoaThenReturnSuccess() {
    /*
    Pass the instance of Pessoa who owns of Telefone who is being saved and pass also the instance of Telefone
    later to be saved. Iterate the Telefone list of this passed Pessoa and add the Telefone to it
    */
    telefoneService.atualizarListaDeTelefonesDaPessoa(pessoa, telefone);

    assertNotNull(pessoa.getTelefones());
    assertEquals(ID, pessoa.getTelefones().get(0).getId());
  }

  @Test
  void whenAtualizarListaDeTelefonesDaPessoaWithPersonWithOtherTelefoneThenReturnSuccess() {
    /*
    In this case, the person already has one Telefone in Telefone list. We're going add a new Telefone on the list,
    causing the list come to have two Telefone registers
    */

    Telefone telefone1 = Telefone.builder()
                    .id(2L)
                    .numero("01000000000")
                    .pessoa(pessoa)
                    .build();

    List<Telefone> telefones = new ArrayList<>();
    telefones.add(telefone1);
    pessoa.setTelefones(telefones);

    telefoneService.atualizarListaDeTelefonesDaPessoa(pessoa, telefone);

    assertNotNull(pessoa.getTelefones());
    assertEquals(ID, pessoa.getTelefones().get(1).getId());
  }

  @Test
  void whenCriarTelefoneResponseDTOThenReturnSuccess() {
    var response = telefoneService.criarTelefoneResponseDTO(telefone);

    assertNotNull(response);
    assertEquals(TelefoneResponseDTO.class, response.getClass());
    assertEquals(telefone.getId(), response.getId());
  }

  @Test
  void whenCriarLinkHateoasSelfrelThenReturnSuccess() {
    telefoneService.criarLinkHateoasSelfrel(telefoneResponseDTO);

    assertEquals("</api/telefones/1>;rel=\"self\"", telefoneResponseDTO.getLinks().toString());
  }

  @Test
  void whenCriarLinkHateoasProprietarioThenReturnSuccess() {
    telefoneService.criarLinkHateoasProprietario(telefoneResponseDTO, telefone);

    assertEquals("</api/pessoas/1>;rel=\"Proprietário\"", telefoneResponseDTO.getLinks().toString());
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

  public void startAttributes() {
    pessoa = Pessoa.builder()
            .id(ID)
            .nome("Nome Test")
            .sexo("MASCULINO")
            .build();

    telefoneRequestDTO = TelefoneRequestDTO.builder()
            .numero(NUMERO)
            .pessoaId(ID)
            .build();

    telefone = Telefone.builder()
            .id(ID)
            .numero(NUMERO)
            .pessoa(pessoa)
            .build();

    telefoneResponseDTO = TelefoneResponseDTO.builder()
            .id(ID)
            .numero(NUMERO)
            .build();
  }

}