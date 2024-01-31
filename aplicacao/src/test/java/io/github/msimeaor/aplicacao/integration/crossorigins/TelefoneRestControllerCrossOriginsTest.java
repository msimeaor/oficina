package io.github.msimeaor.aplicacao.integration.crossorigins;

import io.github.msimeaor.aplicacao.config.TestConfigs;
import io.github.msimeaor.aplicacao.integration.dto.request.TelefoneRequestDTOTest;
import io.github.msimeaor.aplicacao.integration.testcontainer.AbstractIntegrationTest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TelefoneRestControllerCrossOriginsTest extends AbstractIntegrationTest {

  private static RequestSpecification specification;
  private static TelefoneRequestDTOTest telefoneRequestDTOTest;

  private static final String REQUEST_BASE_PATH = "api/telefones";

  @BeforeEach
  void setUp() {
    startTestEntities();
  }

  @Test
  @Order(1)
  void save() {
    specification = new RequestSpecBuilder()
            .setPort(TestConfigs.SERVER_PORT)
            .addHeader(TestConfigs.ORIGIN, TestConfigs.WRONG_ORIGIN)
            .setContentType(MediaType.APPLICATION_JSON_VALUE)
            .build();

    var content = given().spec(specification)
            .basePath(REQUEST_BASE_PATH)
            .body(telefoneRequestDTOTest)
            .when()
              .post()
            .then()
              .statusCode(403)
            .extract()
              .body()
                .asString();

    assertNotNull(content);
    assertEquals("Invalid CORS request", content.toString());
  }

  @Test
  @Order(2)
  void findById() {
    var content = given().spec(specification)
            .basePath(REQUEST_BASE_PATH)
            .pathParam("id", "1")
            .when()
              .get("{id}")
            .then()
              .statusCode(403)
            .extract()
              .body()
                .asString();

    assertNotNull(content);
    assertEquals("Invalid CORS request", content.toString());
  }

  @Test
  @Order(3)
  void findAll() {
    var content = given().spec(specification)
            .basePath(REQUEST_BASE_PATH)
            .when()
              .get()
            .then()
              .statusCode(403)
            .extract()
              .body()
                .asString();

    assertNotNull(content);
    assertEquals("Invalid CORS request", content.toString());
  }

  @Test
  @Order(4)
  void update() {
    var content = given().spec(specification)
            .basePath(REQUEST_BASE_PATH)
            .body(telefoneRequestDTOTest)
            .pathParam("id", "1")
            .when()
              .put("{id}")
            .then()
              .statusCode(403)
            .extract()
              .body()
                .asString();

    assertNotNull(content);
    assertEquals("Invalid CORS request", content.toString());
  }

  @Test
  @Order(5)
  void findByNumero() {
    var content = given().spec(specification)
            .basePath(REQUEST_BASE_PATH)
            .pathParam("numero", "61991979110")
            .when()
              .get("{numero}")
            .then()
              .statusCode(403)
            .extract()
              .body()
                .asString();

    assertNotNull(content);
    assertEquals("Invalid CORS request", content.toString());
  }

  public static void startTestEntities() {
    telefoneRequestDTOTest = TelefoneRequestDTOTest.builder()
            .numero("61991979110")
            .pessoaId(71L)
            .build();
  }
}