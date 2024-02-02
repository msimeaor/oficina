package io.github.msimeaor.aplicacao.model.service.impl;

import io.github.msimeaor.aplicacao.exceptions.servico.ServicoConflictException;
import io.github.msimeaor.aplicacao.model.dto.request.ServicoRequestDTO;
import io.github.msimeaor.aplicacao.model.dto.response.ServicoResponseDTO;
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
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Optional;

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
  void findById() {
  }

  @Test
  void findByNome() {
  }

  @Test
  void findAll() {
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
  }

}