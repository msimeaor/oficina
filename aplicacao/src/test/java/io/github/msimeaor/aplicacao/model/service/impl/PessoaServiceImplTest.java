package io.github.msimeaor.aplicacao.model.service.impl;

import io.github.msimeaor.aplicacao.enums.Fabricantes;
import io.github.msimeaor.aplicacao.enums.UFs;
import io.github.msimeaor.aplicacao.exceptions.geral.EmptyListException;
import io.github.msimeaor.aplicacao.exceptions.pessoa.PessoaConflictException;
import io.github.msimeaor.aplicacao.model.dto.request.PessoaRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.PessoaResponseDTO;
import io.github.msimeaor.aplicacao.model.entity.Endereco;
import io.github.msimeaor.aplicacao.model.entity.Pessoa;
import io.github.msimeaor.aplicacao.model.entity.Telefone;
import io.github.msimeaor.aplicacao.model.entity.Veiculo;
import io.github.msimeaor.aplicacao.model.repository.PessoaRepository;
import io.github.msimeaor.aplicacao.model.repository.VeiculoRepository;
import io.github.msimeaor.aplicacao.model.service.utilities.EnderecoUtilitiesService;
import io.github.msimeaor.aplicacao.model.service.utilities.PessoaUtilitiesService;
import io.github.msimeaor.aplicacao.model.service.utilities.TelefoneUtilitiesService;
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
class PessoaServiceImplTest {

  @InjectMocks
  private PessoaServiceImpl pessoaService;
  @Mock
  private PessoaRepository repository;
  @Mock
  private VeiculoRepository veiculoRepository;
  @Mock
  private PessoaUtilitiesService pessoaUtilitiesService;
  @Mock
  private EnderecoUtilitiesService enderecoUtilitiesService;
  @Mock
  private TelefoneUtilitiesService telefoneUtilitiesService;
  @Mock
  private PagedResourcesAssembler<PessoaResponseDTO> assembler;

  private PessoaRequestDTO pessoaRequestDTO;
  private Pessoa pessoa;
  private Endereco endereco;
  private Telefone telefone;
  private Veiculo veiculo;
  private Pageable pageable;
  private Page<Pessoa> pessoaPage;
  private PagedModel<EntityModel<PessoaResponseDTO>> pessoaPagedModel;

  private static final String NOME = "Nome Test";
  private static final String SEXO = "MASCULINO";
  private static final Long ID = 1L;
  private static final String PLACA = "JJJ1111";
  private static final String LOGRADOURO = "Logradouro Test";
  private static final UFs UF = UFs.DF;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    initializeTestsEntities();
  }

  @Test
  void whenSaveThenReturnSuccess() {
    when(repository.findByNome(anyString())).thenReturn(Optional.empty());
    when(enderecoUtilitiesService.buscarEndereco(anyLong())).thenReturn(endereco);
    when(repository.save(any(Pessoa.class))).thenReturn(pessoa);

    var response = pessoaService.save(pessoaRequestDTO, PLACA);

    assertNotNull(response);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(Objects.requireNonNull(response.getBody()).getClass(), PessoaResponseDTO.class);

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
  void whenFindByIdThenReturnSuccess() {
    when(pessoaUtilitiesService.buscarPessoa(anyLong())).thenReturn(pessoa);

    var response = pessoaService.findById(ID);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(Objects.requireNonNull(response.getBody()).getClass(), PessoaResponseDTO.class);

    assertEquals(ID ,response.getBody().getId());
    assertEquals("</api/pessoas/1>;rel=\"self\"", response.getBody().getLinks().toString());
  }

  @Test
  void whenFindAllThenReturnSuccess() {
    when(repository.findAll(any(Pageable.class))).thenReturn(pessoaPage);
    when(assembler.toModel(any(Page.class), any(Link.class)))
            .thenReturn(pessoaPagedModel);

    var response = pessoaService.findAll(pageable);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(PagedModel.class, Objects.requireNonNull(response.getBody()).getClass());

    // Here I verify if the person returned list have person valid registers
    response.getBody().getContent().forEach(e -> {
      assertNotNull(e.getContent());
      assertEquals(PessoaResponseDTO.class, e.getContent().getClass());
      assertEquals(ID, e.getContent().getId());
    });

    // Here I verify if pagination info of response are valid
    assertNotNull(response.getBody().getMetadata());
    assertEquals(0, response.getBody().getMetadata().getNumber());
    assertEquals(1, response.getBody().getMetadata().getTotalPages());
    assertEquals(1, response.getBody().getMetadata().getTotalElements());
    assertEquals(1, response.getBody().getMetadata().getSize());
  }

  @Test
  void whenCriarPagePessoaThenReturnSuccess() {
    when(repository.findAll(any(Pageable.class))).thenReturn(pessoaPage);

    var response = pessoaService.criarPagePessoa(pageable);

    assertNotNull(response);
    assertEquals(PageImpl.class, response.getClass());
    response.forEach(p -> {
      assertNotNull(p);
      assertEquals(Pessoa.class, p.getClass());
      assertEquals(ID, p.getId());
    });
  }

  @Test
  void whenCriarPagePessoaThenReturnEmptyListException() {
    when(repository.findAll(any(Pageable.class))).thenReturn(Page.empty());

    try {
      pessoaService.criarPagePessoa(pageable);

    } catch (Exception ex) {
      assertEquals(EmptyListException.class, ex.getClass());
      assertEquals("Não existem clientes cadastrados!", ex.getMessage());
    }
  }

  @Test
  void whenConverterPagePessoaEmPagePessoaResponseDTOThenReturnSuccess() {
    var response = pessoaService.converterPagePessoaEmPagePessoaResponseDTO(pessoaPage);

    assertNotNull(response);
    assertEquals(PageImpl.class, response.getClass());
    response.forEach(p -> {
      assertNotNull(p);
      assertEquals(PessoaResponseDTO.class, p.getClass());
      assertEquals(ID, p.getId());
    });
  }

  @Test
  void whenUpdateThenReturnSuccess() {
    when(pessoaUtilitiesService.buscarPessoa(anyLong())).thenReturn(pessoa);
    when(enderecoUtilitiesService.buscarEndereco(anyLong())).thenReturn(endereco);
    pessoa.setNome("Nome updated");
    when(repository.save(any(Pessoa.class))).thenReturn(pessoa);

    var response = pessoaService.update(pessoaRequestDTO, ID);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(ID, Objects.requireNonNull(response.getBody()).getId());
    assertEquals("Nome updated", response.getBody().getNome());
  }

  @Test
  void whenFindByNomeLikeThenReturnSuccess() {
    when(repository.findByNomeLike(anyString(), any(Pageable.class))).thenReturn(pessoaPage);
    when(assembler.toModel(any(Page.class), any(Link.class)))
            .thenReturn(pessoaPagedModel);

    var response = pessoaService.findByNomeLike("%" + NOME + "%", pageable);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(PagedModel.class, Objects.requireNonNull(response.getBody()).getClass());

    // Here I verify if the person returned list have person valid registers
    response.getBody().getContent().forEach(e -> {
      assertNotNull(e.getContent());
      assertEquals(PessoaResponseDTO.class, e.getContent().getClass());
      assertEquals(ID, e.getContent().getId());
    });

    // Here I verify if pagination info of response are valid
    assertNotNull(response.getBody().getMetadata());
    assertEquals(0, response.getBody().getMetadata().getNumber());
    assertEquals(1, response.getBody().getMetadata().getTotalPages());
    assertEquals(1, response.getBody().getMetadata().getTotalElements());
    assertEquals(1, response.getBody().getMetadata().getSize());
  }

  @Test
  void whenCriarPagePessoaComFindByNomeLikeThenReturnSuccess() {
    when(repository.findByNomeLike(anyString(), any(Pageable.class)))
            .thenReturn(pessoaPage);

    var response = pessoaService.criarPagePessoaComFindByNomeLike("%" + NOME + "%", pageable);

    assertNotNull(response);
    assertEquals(PageImpl.class, response.getClass());
    response.forEach(p -> {
      assertNotNull(p);
      assertEquals(Pessoa.class, p.getClass());
      assertEquals(ID, p.getId());
      assertTrue(p.getNome().contains(NOME));
    });
  }

  @Test
  void whenCriarPagePessoaComFindByNomeLikeThenReturnEmptyListException() {
    when(repository.findByNomeLike(anyString(), any(Pageable.class)))
            .thenReturn(Page.empty());

    try {
      pessoaService.criarPagePessoaComFindByNomeLike("%" + NOME + "%", pageable);

    } catch (Exception ex) {
      assertNotNull(ex);
      assertEquals(EmptyListException.class, ex.getClass());
      assertEquals("Não existem clientes cadastrados!", ex.getMessage());
    }

  }

  @Test
  void whenValidarPageSizeThenThrowEmptyListException() {
    try {
      pessoaService.validarPageSize(Page.empty());
    } catch (Exception ex) {
      assertNotNull(ex);
      assertEquals(EmptyListException.class, ex.getClass());
      assertEquals("Não existem clientes cadastrados que tenham esse nome!", ex.getMessage());
    }
  }

  public void initializeTestsEntities() {
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

    PessoaResponseDTO pessoaResponseDTO = PessoaResponseDTO.builder()
            .id(ID)
            .nome(NOME)
            .sexo(SEXO)
            .build();

    pageable = PageRequest.of(0, 10);
    List<Pessoa> pessoas = Collections.singletonList(pessoa);
    pessoaPage = new PageImpl<>(pessoas, pageable, pessoas.size());

    List<EntityModel<PessoaResponseDTO>> entityModels = new ArrayList<>();
    EntityModel<PessoaResponseDTO> entityModel = EntityModel.of(pessoaResponseDTO);
    entityModels.add(entityModel);
    PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(entityModels.size(),
            0, entityModels.size());
    pessoaPagedModel = PagedModel.of(entityModels, pageMetadata);
  }

}