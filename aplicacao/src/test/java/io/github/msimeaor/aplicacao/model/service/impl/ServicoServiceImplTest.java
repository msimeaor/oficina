package io.github.msimeaor.aplicacao.model.service.impl;

import io.github.msimeaor.aplicacao.exceptions.servico.ServicoConflictException;
import io.github.msimeaor.aplicacao.exceptions.servico.ServicoNotFoundException;
import io.github.msimeaor.aplicacao.integration.dto.response.ServicoResponseDTOTest;
import io.github.msimeaor.aplicacao.model.dto.request.ServicoRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.ServicoResponseDTO;
import io.github.msimeaor.aplicacao.model.dto.response.TelefoneResponseDTO;
import io.github.msimeaor.aplicacao.model.entity.Servico;
import io.github.msimeaor.aplicacao.model.repository.ServicoRepository;
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

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class ServicoServiceImplTest {

  @InjectMocks
  private ServicoServiceImpl servicoService;
  @Mock
  private ServicoRepository repository;
  @Mock
  private PagedResourcesAssembler<ServicoResponseDTO> assembler;

  private Servico servico;
  private ServicoRequestDTO servicoRequestDTO;
  private ServicoResponseDTO servicoResponseDTO;
  private Pageable pageable;
  private Page<Servico> servicoPage;
  private PagedModel<EntityModel<ServicoResponseDTO>> servicoPagedModel;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    startTestEntities();
  }

  @Test
  void whenSaveThenReturnSuccess() {
    when(repository.findByNome(anyString())).thenReturn(Optional.empty());
    when(repository.save(any(Servico.class))).thenReturn(servico);

    var response = servicoService.save(servicoRequestDTO);

    assertNotNull(response);
    assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
    assertEquals(ServicoResponseDTO.class, response.getBody().getClass());
    assertEquals(1L, response.getBody().getId());
    assertEquals("Serviço Teste", response.getBody().getNome());
    assertEquals(BigDecimal.valueOf(10000, 2), response.getBody().getValor());
  }

  @Test
  void whenSaveThenThrowServicoConflictException() {
    when(repository.findByNome(anyString())).thenReturn(Optional.of(servico));

    try {
      servicoService.save(servicoRequestDTO);

    } catch (Exception ex) {
      assertNotNull(ex);
      assertEquals(ServicoConflictException.class, ex.getClass());
      assertEquals("Serviço já cadastrado!", ex.getMessage());
    }
  }

  @Test
  void whenFindByIdThenReturnSuccess() {
    when(repository.findById(anyLong())).thenReturn(Optional.of(servico));

    var response = servicoService.findById(1L);

    assertNotNull(response);
    assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
    assertEquals(ServicoResponseDTO.class, response.getBody().getClass());
    assertEquals(1L, response.getBody().getId());
    assertEquals("Serviço Teste", response.getBody().getNome());
    assertEquals(BigDecimal.valueOf(10000, 2), response.getBody().getValor());
  }

  @Test
  void whenFindByIdThenReturnServicoNotFound() {
    when(repository.findById(anyLong())).thenReturn(Optional.empty());

    try {
      servicoService.findById(2L);

    } catch (Exception ex) {
      assertNotNull(ex);
      assertEquals(ServicoNotFoundException.class, ex.getClass());
      assertEquals("Serviço não encontrado! ID: 2", ex.getMessage());
    }
  }

  @Test
  void whenFindByNomeThenReturnSuccess() {
    when(repository.findByNome(anyString(), any(Pageable.class))).thenReturn(servicoPage);
    when(assembler.toModel(any(Page.class), any(Link.class)))
            .thenReturn(servicoPagedModel);

    var response = servicoService.findByNome("Serviço Teste", pageable);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(PagedModel.class, Objects.requireNonNull(response.getBody()).getClass());

    response.getBody().getContent().forEach(e -> {
      assertNotNull(e.getContent());
      assertEquals(ServicoResponseDTO.class, e.getContent().getClass());
      assertEquals(1L, e.getContent().getId());
      assertEquals("Servico Teste", e.getContent().getNome());
      assertEquals(BigDecimal.valueOf(10000, 2), e.getContent().getValor());
    });

    // Here I verify if pagination info of response are valid
    assertNotNull(response.getBody().getMetadata());
    assertEquals(0, response.getBody().getMetadata().getNumber());
    assertEquals(1, response.getBody().getMetadata().getTotalPages());
    assertEquals(1, response.getBody().getMetadata().getTotalElements());
    assertEquals(1, response.getBody().getMetadata().getSize());
  }

  @Test
  void whenFindAllThenReturnSuccess() {
    when(repository.findAll(any(Pageable.class))).thenReturn(servicoPage);
    when(assembler.toModel(any(Page.class), any(Link.class)))
            .thenReturn(servicoPagedModel);

    var response = servicoService.findAll(pageable);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(PagedModel.class, Objects.requireNonNull(response.getBody()).getClass());

    response.getBody().getContent().forEach(e -> {
      assertNotNull(e.getContent());
      assertEquals(ServicoResponseDTO.class, e.getContent().getClass());
      assertEquals(1L, e.getContent().getId());
      assertEquals("Servico Teste", e.getContent().getNome());
      assertEquals(BigDecimal.valueOf(10000, 2), e.getContent().getValor());
    });

    // Here I verify if pagination info of response are valid
    assertNotNull(response.getBody().getMetadata());
    assertEquals(0, response.getBody().getMetadata().getNumber());
    assertEquals(1, response.getBody().getMetadata().getTotalPages());
    assertEquals(1, response.getBody().getMetadata().getTotalElements());
    assertEquals(1, response.getBody().getMetadata().getSize());
  }

  @Test
  void update() {
  }

  public void startTestEntities() {
    servico = Servico.builder()
            .id(1L)
            .nome("Serviço Teste")
            .valor(BigDecimal.valueOf(10000, 2))
            .build();

    servicoRequestDTO = ServicoRequestDTO.builder()
            .nome("Serviço Teste")
            .valor(BigDecimal.valueOf(10000, 2))
            .build();

    servicoResponseDTO = ServicoResponseDTO.builder()
            .id(1L)
            .nome("Servico Teste")
            .valor(BigDecimal.valueOf(10000, 2))
            .build();

    pageable = PageRequest.of(0, 5);
    List<Servico> servicos = Collections.singletonList(servico);
    servicoPage = new PageImpl<>(servicos, pageable, servicos.size());

    List<EntityModel<ServicoResponseDTO>> entityModels = new ArrayList<>();
    EntityModel<ServicoResponseDTO> entityModel = EntityModel.of(servicoResponseDTO);
    entityModels.add(entityModel);
    PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(entityModels.size(),
            0, entityModels.size());
    servicoPagedModel = PagedModel.of(entityModels, pageMetadata);
  }

}