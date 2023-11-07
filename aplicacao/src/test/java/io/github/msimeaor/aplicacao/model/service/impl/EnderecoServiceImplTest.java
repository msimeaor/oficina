package io.github.msimeaor.aplicacao.model.service.impl;

import io.github.msimeaor.aplicacao.enums.UFs;
import io.github.msimeaor.aplicacao.exceptions.endereco.EnderecoConflictException;
import io.github.msimeaor.aplicacao.exceptions.endereco.EnderecoNotFoundException;
import io.github.msimeaor.aplicacao.exceptions.geral.EmptyListException;
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
  private Pageable pageable;
  private Page<Endereco> enderecoPage;
  private Page<EnderecoResponseDTO> enderecoResponseDTOPage;
  private PagedModel<EntityModel<EnderecoResponseDTO>> enderecoPagedModel;

  private static final Long ID = 1L;
  private static final String LOGRADOURO = "Logradouro Test";
  private static final UFs UF = UFs.DF;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    initializeTestsEntities();
  }

  @Test
  void whenSaveThenReturnSuccess() {
    when(repository.findByLogradouro(anyString())).thenReturn(null);
    when(repository.save(any(Endereco.class))).thenReturn(endereco);

    var response = enderecoService.save(enderecoRequestDTO);

    assertNotNull(response);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(EnderecoResponseDTO.class, response.getBody().getClass());

    assertEquals(ID ,response.getBody().getId());
    assertEquals(LOGRADOURO, response.getBody().getLogradouro());
    assertEquals(UF, response.getBody().getUf());

    assertEquals("</api/enderecos/1>;rel=\"self\"", response.getBody().getLinks().toString());
    // I am not validating the HATEOAS links of residents that are generated in the EnderecoResponseDTO
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
      enderecoService.criarListaPessoaPorId(Collections.singletonList(2L));

    } catch (Exception ex) {
      assertNotNull(ex);
      assertEquals(PessoaNotFoundException.class, ex.getClass());
      assertEquals("Cliente não encontrado! ID: " + 2L, ex.getMessage());
    }
  }

  @Test
  void whenCriarListaPessoaPorIdThenReturnNull() {
    var response = enderecoService.criarListaPessoaPorId(null);

    assertNull(response);
  }

  @Test
  void whenCriarEnderecoESalvarThenReturnSuccess() {
    when(repository.save(any(Endereco.class))).thenReturn(endereco);

    var response = enderecoService.criarEnderecoESalvar(enderecoRequestDTO, Collections.singletonList(pessoa));
    // Simulating the endereco.setPessoas of the method
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
  void whenFindByIdThenReturnSuccess() {
    when(repository.findById(anyLong())).thenReturn(Optional.of(endereco));

    var response = enderecoService.findById(ID);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(response.getBody().getClass(), EnderecoResponseDTO.class);

    assertEquals(ID ,response.getBody().getId());
    // We are not testing the HATEOAS links of the residents.
    assertEquals("</api/enderecos/1>;rel=\"self\"", response.getBody().getLinks().toString());
  }

  @Test
  void whenBuscarEnderecoThenReturnSuccess() {
    when(repository.findById(anyLong())).thenReturn(Optional.of(endereco));

    var response = enderecoService.buscarEndereco(ID);

    assertNotNull(response);
    assertEquals(Endereco.class, response.getClass());
    assertEquals(ID, response.getId());
  }

  @Test
  void whenBuscarEnderecoThenReturnEnderecoNotFoundException() {
    when(repository.findById(anyLong())).thenReturn(Optional.empty());

    try {
      enderecoService.buscarEndereco(2L);

    } catch (Exception ex) {
      assertNotNull(ex);
      assertEquals(EnderecoNotFoundException.class, ex.getClass());
      assertEquals("Endereço não encontrado! ID: " + 2L, ex.getMessage());
    }
  }

  @Test
  void whenFindAllThenReturnSuccess() {
    when(repository.findAll(any(Pageable.class))).thenReturn(enderecoPage);
    when(assembler.toModel(any(Page.class), any(Link.class)))
            .thenReturn(enderecoPagedModel);

    var response = enderecoService.findAll(pageable);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(PagedModel.class, response.getBody().getClass());

    // Here I verify if the address returned list have address valid registers
    response.getBody().getContent().forEach(e -> {
      assertNotNull(e.getContent());
      assertEquals(EnderecoResponseDTO.class, e.getContent().getClass());
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
    Taking into account that this address list only has one address registered. If this list had other registry,
    this hateoas link would contain links to next page, previous page, current page etc.
     */
    assertEquals("</api/enderecos?page=0&size=10&direction=ASC>;rel=\"self\"",
            response.getBody().getLinks().toString());
  }

  @Test
  void whenCriarPageEnderecoThenReturnSuccess() {
    when(repository.findAll(any(Pageable.class))).thenReturn(enderecoPage);

    var response = enderecoService.criarPageEndereco(pageable);

    assertNotNull(response);
    assertEquals(PageImpl.class, response.getClass());
    response.forEach(e -> {
      assertNotNull(e);
      assertEquals(Endereco.class, e.getClass());
      assertEquals(ID, e.getId());
    });
  }

  @Test
  void whenCriarPageEnderecoThenReturnEmptyListException() {
    when(repository.findAll(any(Pageable.class))).thenReturn(Page.empty());

    try {
      enderecoService.criarPageEndereco(pageable);

    } catch (Exception ex) {
      assertNotNull(ex);
      assertEquals(EmptyListException.class, ex.getClass());
      assertEquals("Não existem endereços cadastrados!", ex.getMessage());
    }
  }

  @Test
  void whenConverterPageEnderecoEmPageEnderecoResponseDTOThenReturnSuccess() {
    var response = enderecoService.converterPageEnderecoEmPageEnderecoResponseDTO(enderecoPage);

    assertNotNull(response);
    assertEquals(PageImpl.class, response.getClass());
    response.forEach(e -> {
      assertNotNull(e);
      assertEquals(EnderecoResponseDTO.class, e.getClass());
      assertEquals(ID, e.getId());
    });
  }

  @Test
  void whenCriarLinksHateoasPageEnderecoResponseDTOThenReturnSuccess() {
    enderecoService.criarLinksHateoasPageEnderecoResponseDTO(enderecoResponseDTOPage, enderecoPage);


    /*
    The method call take all EnderecoResponseDTO passed in this page and added the Selfrel HATEOAS link for each one of them.
    The residents HATEOAS link is not being validating
    */
    enderecoResponseDTOPage.forEach(e -> {
      assertNotNull(e);
      assertEquals("</api/enderecos/1>;rel=\"self\"", e.getLinks().toString());
    });
  }

  @Test
  void whenCriarLinkHateoasNavegacaoEntrePaginasThenReturnSuccess() {
    var response = enderecoService.criarLinkHateoasNavegacaoEntrePaginas(pageable);

    assertNotNull(response);
    assertEquals(Link.class, response.getClass());
    assertTrue(response.toString().startsWith("</api/enderecos"));
  }

  @Test
  void whenUpdateThenReturnSuccess() {
    when(repository.findByLogradouro(anyString())).thenReturn(null);
    when(repository.findById(anyLong())).thenReturn(Optional.of(endereco));
    endereco.setLogradouro("Logradouro Updated");
    when(repository.save(any(Endereco.class))).thenReturn(endereco);

    var response = enderecoService.update(enderecoRequestDTO, ID);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(EnderecoResponseDTO.class, response.getBody().getClass());

    assertEquals(ID ,response.getBody().getId());
    // We are not validating residents HATEOAS links
    assertEquals("</api/enderecos/1>;rel=\"self\"", response.getBody().getLinks().toString());
  }

  @Test
  void whenAtualizarEnderecoESalvarThenReturnSuccess() {
    when(repository.save(any(Endereco.class))).thenReturn(endereco);

    var response = enderecoService.atualizarEnderecoESalvar(enderecoRequestDTO,
            Collections.singletonList(pessoa),
            ID);

    // Simulating the method line that add a person list of the parameter in the address
    response.setPessoas(Collections.singletonList(pessoa));

    assertNotNull(response);
    assertEquals(Endereco.class, response.getClass());
    assertEquals(ID, response.getId());
    assertNotNull(response.getPessoas());
    assertEquals(ID, response.getPessoas().get(0).getId());
  }

  private void initializeTestsEntities() {
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

    pageable = PageRequest.of(0, 10);
    List<Endereco> enderecos = Collections.singletonList(endereco);
    enderecoPage = new PageImpl<>(enderecos, pageable, enderecos.size());

    List<EnderecoResponseDTO> enderecoResponseDTOS = Collections.singletonList(enderecoResponseDTO);
    enderecoResponseDTOPage = new PageImpl<>(enderecoResponseDTOS, pageable, enderecoResponseDTOS.size());

    List<EntityModel<EnderecoResponseDTO>> entityModels = new ArrayList<>();
    EntityModel<EnderecoResponseDTO> entityModel = EntityModel.of(enderecoResponseDTO);
    entityModels.add(entityModel);
    PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(entityModels.size(),
            0, entityModels.size());
    Link link = enderecoService.criarLinkHateoasNavegacaoEntrePaginas(pageable);
    enderecoPagedModel = PagedModel.of(entityModels, pageMetadata, link);
  }

}