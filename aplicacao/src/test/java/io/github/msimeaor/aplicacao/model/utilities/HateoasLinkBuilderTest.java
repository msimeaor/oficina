package io.github.msimeaor.aplicacao.model.utilities;

import io.github.msimeaor.aplicacao.controller.PessoaRestController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.Link;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class HateoasLinkBuilderTest {

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void whenGerarLinkThenReturnSuccess() {
    HateoasLinkBuilder hateoasLinkBuilder = new HateoasLinkBuilder();
    Class<?> controllerClass = PessoaRestController.class;
    String nomeMetodo = "findAll";

    Link link = hateoasLinkBuilder.gerarLink(controllerClass, nomeMetodo);

    assertNotNull(link);
    assertEquals("</api/pessoas{?page,size,direction}>;rel=\"self\"", link.toString().trim());
  }

  @Test
  void gerarLinkFiltrando() {
    HateoasLinkBuilder hateoasLinkBuilder = new HateoasLinkBuilder();
    Class<?> controllerClass = PessoaRestController.class;
    String nomeMetodo = "findByNome";
    String filtroBusca = "A";

    Link link = hateoasLinkBuilder.gerarLinkFiltrando(controllerClass, nomeMetodo, filtroBusca);

    assertNotNull(link);
    assertEquals("</api/pessoas/findByNome/A{?page,size,direction}>;rel=\"self\"", link.toString().trim());
  }

  @Test
  void gerarLinkComMetodoInexistente_ResultandoErro() {
    HateoasLinkBuilder hateoasLinkBuilder = new HateoasLinkBuilder();
    Class<?> controllerClass = PessoaRestController.class;
    String nomeMetodo = "metodoInexistente";

    try {
      hateoasLinkBuilder.gerarLink(controllerClass, nomeMetodo);
    } catch (Exception ex) {
      assertNotNull(ex);
      assertEquals(IllegalArgumentException.class, ex.getClass());
    }
  }

}