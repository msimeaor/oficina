package io.github.msimeaor.aplicacao.model.service.impl;

import io.github.msimeaor.aplicacao.exceptions.geral.EmptyListException;
import io.github.msimeaor.aplicacao.exceptions.pessoa.PessoaNotFoundException;
import io.github.msimeaor.aplicacao.exceptions.telefone.TelefoneConflictException;
import io.github.msimeaor.aplicacao.exceptions.telefone.TelefoneNotFoundException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
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
  @Mock
  private PagedResourcesAssembler<TelefoneResponseDTO> assembler;

  private Pessoa pessoa;
  private Telefone telefone;
  private TelefoneRequestDTO telefoneRequestDTO;
  private TelefoneResponseDTO telefoneResponseDTO;
  private Pageable pageable;
  private Page<Telefone> telefonePage;
  private PagedModel<EntityModel<TelefoneResponseDTO>> telefonePagedModel;

  private static final Long ID = 1L;
  private static final String NUMERO = "00000000000";


  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    initializeTestsEntities();
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
      telefoneService.buscarPessoa(2L);

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
  void whenFindByIdThenReturnSuccess() {
    when(repository.findById(anyLong())).thenReturn(Optional.of(telefone));

    var response = telefoneService.findById(ID);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(TelefoneResponseDTO.class, response.getBody().getClass());

    assertEquals(ID ,response.getBody().getId());
    // taking into account that the returned phone has this person on your person list
    assertEquals("</api/telefones/1>;rel=\"self\",</api/pessoas/1>;rel=\"Proprietário\"",
            response.getBody().getLinks().toString());
  }

  @Test
  void whenBuscarTelefoneThenReturnSuccess() {
    when(repository.findById(anyLong())).thenReturn(Optional.of(telefone));

    var response = telefoneService.buscarTelefone(ID);

    assertNotNull(response);
    assertEquals(Telefone.class, response.getClass());
    assertEquals(ID, response.getId());
  }

  @Test
  void whenBuscarTelefoneThenReturnTelefoneNotFoundException() {
    when(repository.findById(anyLong())).thenReturn(Optional.empty());

    try {
      telefoneService.buscarTelefone(2L);

    } catch (Exception ex) {
      assertNotNull(ex);
      assertEquals(TelefoneNotFoundException.class, ex.getClass());
      assertEquals("Telefone não encontrado! ID: " + 2L, ex.getMessage());
    }
  }

  @Test
  void whenFindAllThenReturnSuccess() {
    when(repository.findAll(any(Pageable.class))).thenReturn(telefonePage);
    when(assembler.toModel(any(Page.class), any(Link.class)))
            .thenReturn(telefonePagedModel);

    var response = telefoneService.findAll(pageable);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(PagedModel.class, response.getBody().getClass());

    // Here I verify if the phone returned list have phone valid registers
    response.getBody().getContent().forEach(e -> {
      assertNotNull(e.getContent());
      assertEquals(TelefoneResponseDTO.class, e.getContent().getClass());
      assertEquals(ID, e.getContent().getId());
    });

    // Here I verify if pagination info of response are valid
    assertNotNull(response.getBody().getMetadata());
    assertEquals(0, response.getBody().getMetadata().getNumber());
    assertEquals(1, response.getBody().getMetadata().getTotalPages());
    assertEquals(1, response.getBody().getMetadata().getTotalElements());
    assertEquals(1, response.getBody().getMetadata().getSize());

    /*
    Here I verify if hateoas link of page navigation is correct, based on current page
    Taking into account that this phone list only has one person registered. If this list had other registry,
    this hateoas link would contain links to next page, previous page, current page etc.
     */
    assertEquals("</api/telefones?page=0&size=10&direction=ASC>;rel=\"self\"",
            response.getBody().getLinks().toString());
  }

  @Test
  void whenCriarPageTelefoneThenReturnSuccess() {
    when(repository.findAll(any(Pageable.class))).thenReturn(telefonePage);

    var response = telefoneService.criarPageTelefone(pageable);

    assertNotNull(response);
    assertEquals(PageImpl.class, response.getClass());
    response.forEach(t -> {
      assertNotNull(t);
      assertEquals(Telefone.class, t.getClass());
      assertEquals(ID, t.getId());
    });
  }

  @Test
  void whenCriarPageTelefoneThenReturnEmptyListException() {
    when(repository.findAll(any(Pageable.class))).thenReturn(Page.empty());

    try {
      telefoneService.criarPageTelefone(pageable);

    } catch (Exception ex) {
      assertNotNull(ex);
      assertEquals(EmptyListException.class, ex.getClass());
      assertEquals("Não existem telefones cadastrados!", ex.getMessage());
    }
  }

  @Test
  void whenCriarPageTelefoneResponseDTOThenReturnSuccess() {
    var response = telefoneService.criarPageTelefoneResponseDTO(telefonePage);

    assertNotNull(response);
    assertEquals(PageImpl.class, response.getClass());
    response.forEach(t -> {
      assertNotNull(t);
      assertEquals(TelefoneResponseDTO.class, t.getClass());
      assertEquals(ID, t.getId());
    });
  }

  @Test
  void whenCriarLinkNavegacaoPorPaginasThenReturnSuccess() {
    var response = telefoneService.criarLinkNavegacaoPorPaginas(pageable);

    assertNotNull(response);
    assertEquals(Link.class, response.getClass());
    assertTrue(response.toString().startsWith("</api/telefones?"));
  }

  @Test
  void whenUpdateThenReturnSuccess() {
    when(repository.findByNumero(anyString())).thenReturn(null);
    when(pessoaRepository.findById(anyLong())).thenReturn(Optional.of(pessoa));
    when(repository.findById(anyLong())).thenReturn(Optional.of(telefone));
    telefone.setNumero("02000000000");
    when(repository.save(any(Telefone.class))).thenReturn(telefone);

    var response = telefoneService.update(telefoneRequestDTO, ID);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(TelefoneResponseDTO.class, response.getBody().getClass());

    assertEquals(ID ,response.getBody().getId());
    assertEquals("02000000000", response.getBody().getNumero());
    // This person id was passed in TelefoneRequestDTO.pessoaId, so he must be present in Telefone.pessoa
    assertEquals(pessoa.getId(), telefone.getPessoa().getId());
  }

  @Test
  void whenAtualizarDadosTelefoneThenReturnSuccess() {
    when(repository.save(any(Telefone.class))).thenReturn(telefone);

    var response = telefoneService.atualizarDadosTelefone(telefoneRequestDTO, pessoa, ID);

    assertNotNull(response);
    assertEquals(Telefone.class, response.getClass());
    assertEquals(ID, response.getId());
    assertEquals(pessoa.getId(), response.getPessoa().getId());
  }

  public void initializeTestsEntities() {
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

    pageable = PageRequest.of(0, 10);
    List<Telefone> telefones = Collections.singletonList(telefone);
    telefonePage = new PageImpl<>(telefones, pageable, telefones.size());

    List<EntityModel<TelefoneResponseDTO>> entityModels = new ArrayList<>();
    EntityModel<TelefoneResponseDTO> entityModel = EntityModel.of(telefoneResponseDTO);
    entityModels.add(entityModel);
    PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(entityModels.size(),
            0, entityModels.size());
    Link link = telefoneService.criarLinkNavegacaoPorPaginas(pageable);
    telefonePagedModel = PagedModel.of(entityModels, pageMetadata, link);
  }

}