package io.github.msimeaor.aplicacao.integration.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.msimeaor.aplicacao.config.TestConfigs;
import io.github.msimeaor.aplicacao.enums.Fabricantes;
import io.github.msimeaor.aplicacao.exceptions.ExceptionResponse;
import io.github.msimeaor.aplicacao.integration.dto.request.VeiculoRequestDTOTest;
import io.github.msimeaor.aplicacao.integration.dto.response.VeiculoResponseDTOTest;
import io.github.msimeaor.aplicacao.integration.helper.veiculo.VeiculoWrapper;
import io.github.msimeaor.aplicacao.integration.testcontainer.AbstractIntegrationTest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static io.restassured.RestAssured.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VeiculoRestControllerTest extends AbstractIntegrationTest {

  private static RequestSpecification specification;
  private static ObjectMapper mapper;
  private static VeiculoRequestDTOTest veiculoRequestDTOTest;

  @BeforeEach
  void setUp() {
    mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    mapper.registerModules();
    startEntities();
  }

  @Test
  @Order(1)
  void saveVehicleWithSuccess() throws JsonProcessingException {
    specification = new RequestSpecBuilder()
            .setPort(TestConfigs.SERVER_PORT)
            .addHeader(TestConfigs.ORIGIN, "http://localhost:8080")
            .setContentType(MediaType.APPLICATION_JSON_VALUE)
            .build();

    var content = given().spec(specification)
            .basePath("/api/veiculos")
            .body(veiculoRequestDTOTest)
            .when()
              .post()
            .then()
              .statusCode(201)
            .extract()
              .body()
                .asString();

    var veiculoResponseDTOTest = mapper.readValue(content, VeiculoResponseDTOTest.class);

    assertEquals(VeiculoResponseDTOTest.class, veiculoResponseDTOTest.getClass());
    assertEquals(1L, veiculoResponseDTOTest.getId());
    assertEquals("Veiculo Teste", veiculoResponseDTOTest.getNome());
    assertEquals("AAA0000", veiculoResponseDTOTest.getPlaca());
    assertEquals("100.000", veiculoResponseDTOTest.getKmAtual());
    assertEquals("Observacao Teste", veiculoResponseDTOTest.getObservacao());
    assertEquals(Fabricantes.AUDI, veiculoResponseDTOTest.getFabricante());
  }

  @Test
  @Order(2)
  void saveVehicleWithARepeatedCarPlate() throws JsonProcessingException {
    var content = given().spec(specification)
            .basePath("/api/veiculos")
            .body(veiculoRequestDTOTest)
            .when()
              .post()
            .then()
              .statusCode(409)
            .extract()
              .body()
                .asString();

    var exceptionResponse = mapper.readValue(content, ExceptionResponse.class);

    assertEquals(ExceptionResponse.class, exceptionResponse.getClass());
    assertEquals(HttpStatus.CONFLICT.value(), exceptionResponse.getCodigoStatus());
    assertEquals("Veiculo já cadastrado!", exceptionResponse.getMensagemErro());
    assertEquals("uri=/api/veiculos", exceptionResponse.getDetalhesErro());
  }

  @Test
  @Order(3)
  void findByIdWithAValidId() throws JsonProcessingException {
    var content = given().spec(specification)
            .basePath("/api/veiculos")
            .pathParam("id", 1L)
            .when()
              .get("{id}")
            .then()
              .statusCode(200)
            .extract()
              .body()
                .asString();

    var veiculoResponseDTO = mapper.readValue(content, VeiculoResponseDTOTest.class);

    assertEquals(VeiculoResponseDTOTest.class, veiculoResponseDTO.getClass());
    assertEquals(1L, veiculoResponseDTO.getId());
  }

  @Test
  @Order(4)
  void findByIdWithAnInvalidId() throws JsonProcessingException {
    var content = given().spec(specification)
            .basePath("/api/veiculos")
            .pathParam("id", 2L)
            .when()
              .get("{id}")
            .then()
              .statusCode(404)
            .extract()
              .body()
                .asString();

    var exceptionResponse = mapper.readValue(content, ExceptionResponse.class);

    assertEquals(ExceptionResponse.class, exceptionResponse.getClass());
    assertEquals(HttpStatus.NOT_FOUND.value(), exceptionResponse.getCodigoStatus());
    assertEquals("Veiculo não encontrado! ID: " + 2L, exceptionResponse.getMensagemErro());
    assertEquals("uri=/api/veiculos/2", exceptionResponse.getDetalhesErro());
  }

  @Test
  @Order(5)
  void findAllWithSuccess() throws JsonProcessingException {
    var content = given().spec(specification)
            .basePath("/api/veiculos")
            .when()
              .get()
            .then()
              .statusCode(200)
            .extract()
              .body()
                .asString();

    VeiculoWrapper response = mapper.readValue(content, VeiculoWrapper.class);
    List<VeiculoResponseDTOTest> veiculoResponseDTOTestList = response.getEmbedded().getVeiculoResponseDTOList();

    assertTrue(veiculoResponseDTOTestList.size() > 0);
    assertEquals(1L, veiculoResponseDTOTestList.get(0).getId());
  }

  public static void startEntities() {
    veiculoRequestDTOTest = VeiculoRequestDTOTest.builder()
            .nome("Veiculo Teste")
            .placa("AAA0000")
            .kmAtual("100.000")
            .fabricante(Fabricantes.AUDI)
            .observacao("Observacao Teste")
            .build();
  }

}