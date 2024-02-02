package io.github.msimeaor.aplicacao.integration.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.msimeaor.aplicacao.config.TestConfigs;
import io.github.msimeaor.aplicacao.exceptions.ExceptionResponse;
import io.github.msimeaor.aplicacao.integration.dto.request.ServicoRequestDTOTest;
import io.github.msimeaor.aplicacao.integration.dto.response.ServicoResponseDTOTest;
import io.github.msimeaor.aplicacao.integration.helper.servico.ServicoWrapper;
import io.github.msimeaor.aplicacao.integration.testcontainer.AbstractIntegrationTest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static io.restassured.RestAssured.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ServicoRestControllerTest extends AbstractIntegrationTest {

  private static RequestSpecification specification;
  private static ObjectMapper mapper;
  private static ServicoRequestDTOTest servicoRequestDTOTest;
  private static ServicoRequestDTOTest servicoRequestDTOTestUpdated;

  private static final String REQUEST_BASE_PATH = "api/servicos";

  @BeforeEach
  void setUp() {
    mapper = new ObjectMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    startTestEntities();
  }

  @Test
  @Order(1)
  void save() throws JsonProcessingException {
    specification = new RequestSpecBuilder()
            .setPort(TestConfigs.SERVER_PORT)
            .addHeader(TestConfigs.ORIGIN, "http://localhost:8080")
            .setContentType(MediaType.APPLICATION_JSON_VALUE)
            .build();

    var content = given().spec(specification)
            .basePath(REQUEST_BASE_PATH)
            .body(servicoRequestDTOTest)
            .when()
              .post()
            .then()
              .statusCode(201)
            .extract()
              .body()
                .asString();

    var response = mapper.readValue(content, ServicoResponseDTOTest.class);
    
    assertEquals(ServicoResponseDTOTest.class, response.getClass());
    assertEquals(1, response.getId());
    assertEquals("Serviço Teste", response.getNome());
    assertEquals(BigDecimal.valueOf(10000, 2), response.getValor());
  }

  @Test
  @Order(2)
  void findByIdWithAValidID() throws JsonProcessingException {
    var content = given().spec(specification)
            .basePath(REQUEST_BASE_PATH)
            .pathParam("id", "1")
            .when()
              .get("{id}")
            .then()
              .statusCode(200)
            .extract()
              .body()
                .asString();

    var response = mapper.readValue(content, ServicoResponseDTOTest.class);

    assertEquals(ServicoResponseDTOTest.class, response.getClass());
    assertEquals(1L, response.getId());
    assertEquals("Serviço Teste", response.getNome());
    assertEquals(BigDecimal.valueOf(10000, 2), response.getValor());
  }

  @Test
  @Order(3)
  void findByIdWithAnInvalidID() throws JsonProcessingException {
    var content = given().spec(specification)
            .basePath(REQUEST_BASE_PATH)
            .pathParam("id", 2L)
            .when()
              .get("{id}")
            .then()
              .statusCode(404)
            .extract()
              .body()
                .asString();

    ExceptionResponse exceptionResponse = mapper.readValue(content, ExceptionResponse.class);

    assertNotNull(exceptionResponse);
    assertEquals(HttpStatus.NOT_FOUND.value(), exceptionResponse.getCodigoStatus());
    assertEquals("Serviço não encontrado! ID: " + 2L, exceptionResponse.getMensagemErro());
    assertEquals("uri=/api/servicos/2", exceptionResponse.getDetalhesErro());
  }

  @Test
  @Order(4)
  void findByName() throws JsonProcessingException {
    var content = given().spec(specification)
            .basePath(REQUEST_BASE_PATH + "/findByNome")
            .pathParam("nome", "Serviço Teste")
            .when()
              .get("{nome}")
            .then()
              .statusCode(200)
            .extract()
              .body()
                .asString();

    ServicoWrapper servicoResponse = mapper.readValue(content, ServicoWrapper.class);
    List<ServicoResponseDTOTest> servicoResponseDTOTestList = servicoResponse
            .getServicoEmbedded().getServicoResponseDTOList();

    assertNotNull(content);
    assertNotNull(servicoResponseDTOTestList);
    assertEquals(1L, servicoResponseDTOTestList.get(0).getId());
    assertEquals("Serviço Teste", servicoResponseDTOTestList.get(0).getNome());
    assertEquals(BigDecimal.valueOf(10000, 2), servicoResponseDTOTestList.get(0).getValor());

    assertTrue(content.contains(
      "\"page\":{\"size\":5,\"totalElements\":1,\"totalPages\":1,\"number\":0}"
    ));
    // As the list only contains one result, only the "self" link will be generated.
    // If the list has more results, the "prev", "next", "last" link will be generated.
    // The "%C3%A7" snippet represents the "ç" character
    assertTrue(content.contains(
      "\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/servicos/findByNome/Servi%C3%A7o%20Teste{?page,size,direction}\"" +
              ",\"templated\":true}}"
    ));
  }

  @Test
  @Order(6)
  void findAll() throws JsonProcessingException {
    var content = given().spec(specification)
            .basePath(REQUEST_BASE_PATH)
            .when()
              .get()
            .then()
              .statusCode(200)
            .extract()
              .body()
                .asString();

    ServicoWrapper servicoResponse = mapper.readValue(content, ServicoWrapper.class);
    List<ServicoResponseDTOTest> servicoResponseDTOTestList = servicoResponse
            .getServicoEmbedded().getServicoResponseDTOList();

    assertNotNull(content);
    assertNotNull(servicoResponseDTOTestList);
    assertEquals(1L, servicoResponseDTOTestList.get(0).getId());
    assertEquals("Serviço Teste", servicoResponseDTOTestList.get(0).getNome());
    assertEquals(BigDecimal.valueOf(10000, 2), servicoResponseDTOTestList.get(0).getValor());

    assertTrue(content.contains(
            "\"page\":{\"size\":5,\"totalElements\":1,\"totalPages\":1,\"number\":0}"
    ));
    // As the list only contains one result, only the "self" link will be generated.
    // If the list has more results, the "prev", "next", "last" link will be generated.
    assertTrue(content.contains(
            "\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/servicos{?page,size,direction}\"" +
                    ",\"templated\":true}}"
    ));
  }

  @Test
  @Order(7)
  void update() throws JsonProcessingException {
    var content = given().spec(specification)
            .basePath(REQUEST_BASE_PATH)
            .body(servicoRequestDTOTestUpdated)
            .pathParam("id", 1L)
            .when()
              .put("{id}")
            .then()
              .statusCode(200)
            .extract()
              .body()
                .asString();

    var response = mapper.readValue(content, ServicoResponseDTOTest.class);

    assertNotNull(response);
    assertEquals("Serviço Teste", response.getNome());
    assertEquals(1L, response.getId());
    assertEquals(BigDecimal.valueOf(20000, 2), response.getValor());
  }

  public static void startTestEntities() {
    servicoRequestDTOTest = ServicoRequestDTOTest.builder()
            .nome("Serviço Teste")
            .valor(BigDecimal.valueOf(10000, 2))
            .build();

    servicoRequestDTOTestUpdated = ServicoRequestDTOTest.builder()
            .nome("Serviço Teste")
            .valor(BigDecimal.valueOf(20000, 2))
            .build();
  }
}