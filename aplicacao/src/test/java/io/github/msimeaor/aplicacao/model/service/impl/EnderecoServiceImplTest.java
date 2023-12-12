package io.github.msimeaor.aplicacao.model.service.impl;

import io.github.msimeaor.aplicacao.enums.UFs;
import io.github.msimeaor.aplicacao.exceptions.endereco.EnderecoConflictException;
import io.github.msimeaor.aplicacao.exceptions.geral.EmptyListException;
import io.github.msimeaor.aplicacao.model.dto.request.EnderecoRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.EnderecoResponseDTO;
import io.github.msimeaor.aplicacao.model.entity.Endereco;
import io.github.msimeaor.aplicacao.model.repository.EnderecoRepository;
import io.github.msimeaor.aplicacao.model.service.utilities.EnderecoUtilitiesService;
import io.github.msimeaor.aplicacao.model.service.utilities.PessoaUtilitiesService;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class EnderecoServiceImplTest {

  @InjectMocks
  private EnderecoServiceImpl enderecoService;
  @Mock
  private EnderecoRepository repository;
  @Mock
  private EnderecoUtilitiesService enderecoUtilitiesService;
  @Mock
  private PessoaUtilitiesService pessoaUtilitiesService;
  @Mock
  private PagedResourcesAssembler<EnderecoResponseDTO> assembler;

  private Endereco endereco;
  private EnderecoRequestDTO enderecoRequestDTO;
  private EnderecoResponseDTO enderecoResponseDTO;
  private Pageable pageable;
  private Page<Endereco> enderecoPage;
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
    assertEquals(EnderecoResponseDTO.class, Objects.requireNonNull(response.getBody()).getClass());

    assertEquals(ID ,response.getBody().getId());
    assertEquals(LOGRADOURO, response.getBody().getLogradouro());
    assertEquals(UF, response.getBody().getUf());

    assertEquals("</api/enderecos/1>;rel=\"self\"", response.getBody().getLinks().toString());
    // I am not validating the HATEOAS links of residents that are generated in the EnderecoResponseDTO
  }

  @Test
  void whenSaveThenThrowEnderecoConflictException() {
    when(repository.findByLogradouro(anyString())).thenReturn(endereco);

    try {
      // Saving an address that already exists in the database
      enderecoService.save(enderecoRequestDTO);
    } catch (Exception ex) {
      assertEquals(EnderecoConflictException.class, ex.getClass());
      assertEquals("Logradouro já cadastrado!", ex.getMessage());
    }
  }

  @Test
  void whenFindByIdThenReturnSuccess() {
    when(enderecoUtilitiesService.buscarEndereco(anyLong())).thenReturn(endereco);

    var response = enderecoService.findById(ID);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(Objects.requireNonNull(response.getBody()).getClass(), EnderecoResponseDTO.class);

    assertEquals(ID ,response.getBody().getId());
    // We are not testing the HATEOAS links of the residents.
    assertEquals("</api/enderecos/1>;rel=\"self\"", response.getBody().getLinks().toString());
  }

  @Test
  void whenFindAllThenReturnSuccess() {
    when(repository.findAll(any(Pageable.class))).thenReturn(enderecoPage);
    when(assembler.toModel(any(Page.class), any(Link.class)))
            .thenReturn(enderecoPagedModel);

    var response = enderecoService.findAll(pageable);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(PagedModel.class, Objects.requireNonNull(response.getBody()).getClass());

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
  }

  @Test
  void whenFindAllThenThrowEmptyListException() {
    when(repository.findAll(any(Pageable.class))).thenReturn(Page.empty());

    try {
      // Searching all address when there is no address in the database
      enderecoService.findAll(pageable);
    } catch (Exception ex) {
      assertEquals(EmptyListException.class, ex.getClass());
      assertEquals("Não existem endereços cadastrados!", ex.getMessage());
    }
  }

  @Test
  void whenUpdateThenReturnSuccess() {
    when(repository.findByLogradouro(anyString())).thenReturn(null);
    when(enderecoUtilitiesService.buscarEndereco(anyLong())).thenReturn(endereco);
    endereco.setLogradouro("Logradouro Updated");
    when(repository.save(any(Endereco.class))).thenReturn(endereco);

    var response = enderecoService.update(enderecoRequestDTO, ID);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(EnderecoResponseDTO.class, Objects.requireNonNull(response.getBody()).getClass());

    assertEquals(ID ,response.getBody().getId());
    // We are not validating residents HATEOAS links
    assertEquals("</api/enderecos/1>;rel=\"self\"", response.getBody().getLinks().toString());
  }

  @Test
  void whenFindByLogradouroLikeThenReturnSuccess() {
    when(repository.findByLogradouro(any(String.class), any(Pageable.class)))
            .thenReturn(enderecoPage);
    when(assembler.toModel(any(Page.class), any(Link.class)))
            .thenReturn(enderecoPagedModel);

    var response = enderecoService.findByLogradouro(LOGRADOURO, pageable);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(PagedModel.class, Objects.requireNonNull(response.getBody()).getClass());

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
  }

  @Test
  void whenFindByLogradouroThenReturnEmptyListException() {
    when(repository.findByLogradouro(any(String.class), any(Pageable.class)))
            .thenReturn(Page.empty());

    try {
      enderecoService.findByLogradouro("Logradouro Errado", pageable);
    } catch (Exception ex) {
      assertEquals(EmptyListException.class, ex.getClass());
      assertEquals("Não existem endereços cadastrados com este logradouro!", ex.getMessage());
    }
  }

  private void initializeTestsEntities() {
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

    List<EntityModel<EnderecoResponseDTO>> entityModels = new ArrayList<>();
    EntityModel<EnderecoResponseDTO> entityModel = EntityModel.of(enderecoResponseDTO);
    entityModels.add(entityModel);
    PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(entityModels.size(),
            0, entityModels.size());
    enderecoPagedModel = PagedModel.of(entityModels, pageMetadata);
  }

}