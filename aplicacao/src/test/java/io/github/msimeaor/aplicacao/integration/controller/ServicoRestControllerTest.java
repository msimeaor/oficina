package io.github.msimeaor.aplicacao.integration.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.msimeaor.aplicacao.config.TestConfigs;
import io.github.msimeaor.aplicacao.integration.dto.request.ServicoRequestDTOTest;
import io.github.msimeaor.aplicacao.integration.dto.response.ServicoResponseDTOTest;
import io.github.msimeaor.aplicacao.integration.testcontainer.AbstractIntegrationTest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static io.restassured.RestAssured.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ServicoRestControllerTest extends AbstractIntegrationTest {

  private static RequestSpecification specification;
  private static ObjectMapper mapper;
  private static ServicoRequestDTOTest servicoRequestDTOTest;

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
  @Order(3)
  void findById() throws JsonProcessingException {
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

  public static void startTestEntities() {
    servicoRequestDTOTest = ServicoRequestDTOTest.builder()
            .nome("Serviço Teste")
            .valor(BigDecimal.valueOf(10000, 2))
            .build();
  }
}